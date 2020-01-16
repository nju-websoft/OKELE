import os
import logging
import tensorflow as tf
import math


def get_logger(log_file):
    logger = logging.getLogger(log_file)
    logger.setLevel(logging.DEBUG)
    fh = logging.FileHandler(log_file)
    fh.setLevel(logging.DEBUG)
    ch = logging.StreamHandler()
    ch.setLevel(logging.INFO)
    formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
    ch.setFormatter(formatter)
    fh.setFormatter(formatter)
    logger.addHandler(ch)
    logger.addHandler(fh)
    return logger


def save_model(sess, model, path, logger):
    checkpoint_path = os.path.join(path, "pr.ckpt")
    if not os.path.exists(checkpoint_path):
        os.makedirs(checkpoint_path)
    model.saver.save(sess, checkpoint_path)
    logger.info("model saved")


def create_model(session, Model_class, path, config, logger):
    # create model, reuse parameters if exists
    model = Model_class(config)

    ckpt = tf.train.get_checkpoint_state(path)
    if ckpt and tf.train.checkpoint_exists(ckpt.model_checkpoint_path):
        logger.info("Reading model parameters from %s" % ckpt.model_checkpoint_path)
        model.saver.restore(session, ckpt.model_checkpoint_path)
    else:
        logger.info("Created model with fresh parameters.")
        session.run(tf.global_variables_initializer())
    return model


# initialize variables
def random_uniform_init(shape, name, dtype=tf.float32):
    with tf.name_scope('uniform_normal'):
        std = 1.0 / math.sqrt(shape[1])
        embeddings = tf.get_variable(name, shape=shape, dtype=dtype,
                                     initializer=tf.initializers.random_normal(stddev=std))
    return tf.nn.l2_normalize(embeddings, 1)


# initialize variables
def truncated_normal_init(shape, name, dtype=tf.float32):
    with tf.name_scope('truncated_normal'):
        std = 1.0 / math.sqrt(shape[1])
        weight = tf.get_variable(name, shape=shape, dtype=dtype, initializer=tf.initializers.truncated_normal(stddev=std))
    return tf.nn.l2_normalize(weight, 1)