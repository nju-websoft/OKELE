# Graph Neural Networks for Property Recommendation
import tensorflow as tf
import numpy as np
from keras.regularizers import l2
from tensorflow.python.keras.layers import Dense
from utils import random_uniform_init


class Model(object):

    def __init__(self, config):
        """
        :param config:
        """
        self.config = config
        self.lr = config['lr']
        self.decay_steps = config['decay_steps']
        self.batch_size = config['batch_size']
        self.entity_dim = config['entity_dim']
        self.relation_dim = config['relation_dim']
        self.entity_size = config['entity_size']
        self.relation_size = config['relation_size']
        self.latent_dim = config['latent_dim']
        self.attention_flag = config['attention_flag']
        self.atten_dim = config['atten_dim']
        self.l2 = config['l2']  # init=0

        self.global_step = tf.Variable(0, trainable=False)
        self.best_dev_precision = tf.Variable(0.0, trainable=False)
        self.best_test_precision = tf.Variable(0.0, trainable=False)
        self.best_dev_ndcg = tf.Variable(0.0, trainable=False)
        self.best_test_ndcg = tf.Variable(0.0, trainable=False)

        # input
        self.e_p_Adj = tf.placeholder(dtype=tf.float32,
                                      shape=[self.entity_size, self.relation_size])
        self.e_e_Adj = tf.placeholder(dtype=tf.float32,
                                      shape=[self.entity_size, self.entity_size])

        self.input_entity = tf.placeholder(dtype=tf.int32, shape=[None])
        self.input_relation = tf.placeholder(dtype=tf.int32, shape=[None])
        self.label = tf.placeholder(dtype=tf.float32, shape=[None])

        self.entity_embedding = random_uniform_init(name="entity_embedding_matrix",
                                                    shape=[self.entity_size, self.entity_dim])
        self.relation_embedding = random_uniform_init(name="relation_embedding_matrix",
                                                      shape=[self.relation_size, self.relation_dim])

        with tf.variable_scope("model_entity", reuse=tf.AUTO_REUSE):
            gcn_output = self.gcn(self.e_p_Adj, self.relation_embedding, self.relation_dim, self.entity_embedding,
                                  self.entity_dim, self.latent_dim, self.attention_flag)

            # Modeling topk entities Interaction information
            if config['entity_knn_number'] > 0:
                entity_edges = tf.reduce_sum(self.e_e_Adj, 1)
                entity_edges = tf.tile(tf.expand_dims(entity_edges, 1), [1, self.entity_dim])
                ave_entity_edges = tf.divide(tf.matmul(self.e_e_Adj, self.entity_embedding), entity_edges)

                w3 = tf.get_variable('w3', shape=[self.entity_dim, self.latent_dim],
                                     initializer=tf.truncated_normal_initializer(mean=0.0, stddev=1))
                b3 = tf.get_variable('b3', shape=[self.latent_dim],
                                     initializer=tf.truncated_normal_initializer(mean=0.0, stddev=1))
                h_e_e = tf.nn.xw_plus_b(ave_entity_edges, w3, b3)  # entity_size*latent_dim

                self.h_e = tf.nn.selu(tf.add(gcn_output, h_e_e))
            else:
                self.h_e = tf.nn.selu(gcn_output)

        with tf.variable_scope("model_relation", reuse=tf.AUTO_REUSE):
            gcn_output = self.gcn(tf.transpose(self.e_p_Adj), self.entity_embedding, self.entity_dim,
                                  self.relation_embedding, self.relation_dim, self.latent_dim, self.attention_flag)

            self.h_r = tf.nn.selu(gcn_output)

        with tf.variable_scope("property_rec", reuse=tf.AUTO_REUSE):
            h_e_1 = tf.nn.embedding_lookup(self.h_e, self.input_entity)  # batch_size * entity_latent_dim
            h_r_1 = tf.nn.embedding_lookup(self.h_r, self.input_relation)  # batch_size * relation_latent_dim
            input_temp = tf.multiply(h_e_1, h_r_1)
            # z = Dense(1, kernel_initializer='lecun_uniform', name='prediction')(input_temp)
            for l_num in range(config['mlp_layer_num'] - 1):
                input_temp = Dense(self.entity_dim, activation='selu', kernel_initializer='lecun_uniform')(input_temp)  # MLP hidden layer
            z = Dense(1, kernel_initializer='lecun_uniform', name='prediction')(input_temp)
            z = tf.squeeze(z)

        self.label = tf.squeeze(self.label)
        self.loss = tf.losses.sigmoid_cross_entropy(self.label, z)
        self.z = tf.sigmoid(z)

        # train
        with tf.variable_scope("optimizer"):
            self.learing_rate = tf.train.polynomial_decay(self.lr, self.global_step, decay_steps=self.decay_steps, end_learning_rate=0.00001, cycle=True)
            self.opt = tf.train.AdamOptimizer(self.learing_rate)
            # apply grad clip to avoid gradient explosion
            self.grads_vars = self.opt.compute_gradients(self.loss)
            capped_grads_vars = [[tf.clip_by_value(g, -self.config["clip"], self.config["clip"]), v]
                                 for g, v in self.grads_vars]

            self.train_op = self.opt.apply_gradients(capped_grads_vars, self.global_step)

        # saver of the model
        self.saver = tf.train.Saver(tf.global_variables(), max_to_keep=5)

    def gcn(self, adj, ner_inputs, ner_dim, self_inputs, self_dim, latent_dim, attention_flag=False):
        """
        Aggregate information from neighbor nodes
        :param adj: Adjacency matrix
        :param attention_flag: GAT flag
        :param ner_inputs: entity or property embedding
        :param ner_dim: ner_inputs dimension
        :param self_inputs:
        :param self_dim: self_dim dimension
        :param latent_dim: output dimension
        :return:
        """
        # aggregate heterogeneous information
        if attention_flag:
            query = tf.tile(tf.reshape(self_inputs, (self_inputs.shape[0], 1, self_inputs.shape[1])), [1, ner_inputs.shape[0], 1])
            key = tf.tile(tf.reshape(ner_inputs, (1, ner_inputs.shape[0], ner_inputs.shape[1])), [self_inputs.shape[0], 1, 1])
            key_query = tf.reshape(tf.concat([key, query], -1), [ner_inputs.shape[0]*self_inputs.shape[0], -1])
            alpha = Dense(self.atten_dim, activation='relu', use_bias=True, kernel_regularizer=l2(self.l2))(key_query)
            alpha = Dense(1, activation='relu', use_bias=True, kernel_regularizer=l2(self.l2))(alpha)
            alpha = tf.reshape(alpha, [self_inputs.shape[0], ner_inputs.shape[0]])
            alpha = tf.multiply(alpha, adj)  # entity_size * relation_size
            alpha_exps = tf.nn.softmax(alpha, 1)
            w1 = tf.get_variable('w1', shape=[ner_dim, latent_dim],
                                 initializer=tf.truncated_normal_initializer(mean=0, stddev=1))
            b1 = tf.get_variable('b1', shape=[latent_dim],
                                 initializer=tf.truncated_normal_initializer(mean=0, stddev=1))
            alpha_exps = tf.tile(tf.expand_dims(alpha_exps, -1), [1, 1, ner_inputs.shape[1]])
            e_r = tf.nn.xw_plus_b(tf.reduce_sum(tf.multiply(alpha_exps, key), 1), w1, b1)
        else:
            edges = tf.reduce_sum(adj, 1)
            edges = tf.tile(tf.expand_dims(edges, 1), [1, ner_dim])
            ave_edges = tf.divide(tf.matmul(adj, ner_inputs), edges)
            w1 = tf.get_variable('w1', shape=[ner_dim, latent_dim],
                                 initializer=tf.truncated_normal_initializer(mean=0, stddev=1))
            b1 = tf.get_variable('b1', shape=[latent_dim],
                                 initializer=tf.truncated_normal_initializer(mean=0, stddev=1))
            e_r = tf.nn.xw_plus_b(ave_edges, w1, b1)

        # aggregate same type information
        w2 = tf.get_variable('w2', shape=[self_dim, latent_dim],
                             initializer=tf.truncated_normal_initializer(mean=0, stddev=1))
        b2 = tf.get_variable('b2', shape=[latent_dim],
                             initializer=tf.truncated_normal_initializer(mean=0, stddev=1))
        e_e = tf.nn.xw_plus_b(self_inputs, w2, b2)

        return tf.add(e_r, e_e)

    def run_step(self, sess, is_train, batch):
        """
        :param sess: session to run the batch
        :param is_train: a flag indicate if it is a train batch
        :param batch: a dict containing batch data
        :return: batch result, loss of the batch or logits
        """
        entity_relation_Adj, entity_entity_Adj, input_entity, input_relation, label = batch
        feed_dict = {
            self.e_p_Adj: np.asarray(entity_relation_Adj),
            self.e_e_Adj: np.asarray(entity_entity_Adj),
            self.input_entity: np.asarray(input_entity),
            self.input_relation: np.asarray(input_relation),
            self.label: np.asarray(label)
        }
        if is_train:
            global_step, loss, z, grads_vars, _ = sess.run(
                [self.global_step, self.loss, self.z, self.grads_vars, self.train_op], feed_dict)
            return global_step, loss, z, grads_vars
        else:
            z = sess.run([self.z], feed_dict)
            return z
