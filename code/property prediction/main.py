import argparse
import os
import time
from collections import OrderedDict
from operator import itemgetter

import tensorflow as tf
import numpy as np
import heapq
import sys
import pickle

sys.path.append(".")
from loader import data_reader, BatchManager
from model import Model
from evaluation import precision_at_k, ap, ndcg_at_k
from utils import get_logger, create_model, save_model


def parse_args():
    parser = argparse.ArgumentParser(description="Run PRGNN.")
    parser.add_argument('--domain', nargs='?', default='book.book', help='Choose a dataset.')
    parser.add_argument('--epochs', type=int, default=100, help='Number of epochs.')
    parser.add_argument('--batch_size', type=int, default=512, help='Batch size.')
    parser.add_argument('--num_neg', type=int, default=4,
                        help='Number of negative instances to pair with a positive instance.')
    parser.add_argument('--lr', type=float, default=0.1, help='init Learning rate.')
    parser.add_argument('--k', type=int, default=10, help='top k.')
    parser.add_argument('--mode', type=str, default='train', help='mode')
    parser.add_argument('--entity_dim', type=int, default=16)
    parser.add_argument('--relation_dim', type=int, default=16)
    parser.add_argument('--latent_dim', type=int, default=16)
    parser.add_argument('--atten_dim', type=int, default=16)
    parser.add_argument('--attention_flag', type=int, default=0)  # 0 is False, 1 is true
    parser.add_argument('--l2', type=float, default=0)
    parser.add_argument('--steps_check', type=int, default=100)
    parser.add_argument('--entity_knn_number', type=int, default=100)
    parser.add_argument("--simulate", type=int, default=1)
    parser.add_argument("--datasettype", type=str, default="real-world")  # real-world or synthetic
    parser.add_argument("--mlp_layer_num", type=int, default=2)

    return parser.parse_args()


args = parse_args()
domain = args.domain
k = args.k
lr = args.lr
batch_size = args.batch_size
max_epoch = args.epochs
mode = args.mode
entity_dim = args.entity_dim
relation_dim = args.relation_dim
latent_dim = args.latent_dim
attention_flag = args.attention_flag
atten_dim = args.atten_dim
l2 = args.l2
steps_check = args.steps_check
num_negatives = args.num_neg
entity_knn_number = args.entity_knn_number
simulate = args.simulate
datasettype = args.datasettype
mlp_layer_num = args.mlp_layer_num


def config_model():
    config = OrderedDict()
    config['lr'] = lr
    config['batch_size'] = batch_size
    config['entity_dim'] = entity_dim
    config['relation_dim'] = relation_dim
    config['latent_dim'] = latent_dim
    config['attention_flag'] = (attention_flag == 1)
    config['atten_dim'] = atten_dim
    config["clip"] = 5
    config['ckpt_path'] = "ckpt/" + domain + "/"
    config['log_path'] = "log/" + domain
    config['max_epoch'] = max_epoch
    config['steps_check'] = steps_check
    config['l2'] = l2
    config['num_negatives'] = num_negatives
    config['mode'] = mode
    config['entity_knn_number'] = entity_knn_number
    config['decay_steps'] = 100
    config['simulate'] = simulate
    config['mlp_layer_num'] = mlp_layer_num
    return config


config = config_model()


def train():
    log_path = os.path.join(".", config['log_path'])
    logger = get_logger(log_path)
    map_file_path = "./pkl/" + domain + "/data.pkl"
    if os.path.isfile(map_file_path):
        with open(map_file_path, "rb") as f:
            id2mid, id2p, entity_entity_sim_Matrix, entity_relation_Adj, truth_label, \
            train_entity_list, test_entity_list, valid_entity_list, entity_size, relation_size = pickle.load(f)
            config['entity_size'] = entity_size
            config['relation_size'] = relation_size
    else:
        id2mid, id2p, entity_entity_sim_Matrix, entity_relation_Adj, truth_label, train_entity_list, \
        test_entity_list, valid_entity_list = data_reader(logger, config=config, domain=domain,
                                                          entity_entity_topk=entity_knn_number)
        with open(map_file_path, "wb") as f:
            pickle.dump([id2mid, id2p, entity_entity_sim_Matrix, entity_relation_Adj, truth_label,
                         train_entity_list, test_entity_list, valid_entity_list, config['entity_size'],
                         config['relation_size']], f)

    train_data = (entity_relation_Adj, entity_entity_sim_Matrix, train_entity_list)
    valid_data = (entity_relation_Adj, entity_entity_sim_Matrix, valid_entity_list, truth_label)
    test_data = (entity_relation_Adj, entity_entity_sim_Matrix, test_entity_list, truth_label)
    train_manager = BatchManager(train_data, config['batch_size'], "train", num_negatives=config['num_negatives'])
    valid_manager = BatchManager(valid_data, config['batch_size'], 'valid')
    test_manager = BatchManager(test_data, config['batch_size'], "test")

    tf_config = tf.ConfigProto()
    tf_config.gpu_options.allow_growth = True
    steps_per_epoch = train_manager.len_data
    config['decay_steps'] = 100 * steps_per_epoch
    with tf.Session(config=tf_config) as sess:
        model = create_model(sess, Model, config['ckpt_path'], config, logger)
        logger.info("start training")
        loss_list = []

        for i in range(config['max_epoch']):
            lr = model.learing_rate.eval()
            for batch in train_manager.iter_batch(shuffle=True):
                step, loss, z, grads_vars = model.run_step(sess, True, batch)
                loss_list.append(loss)
                # print loss info
                if step % config['steps_check'] == 0:
                    iteration = step // steps_per_epoch + 1
                    logger.info("iteration:{} step:{}/{}, loss:{:>9.6f}".format(
                        iteration, step % steps_per_epoch, steps_per_epoch, np.mean(loss_list)))
                    loss_list = []

            train_manager = BatchManager(train_data, config['batch_size'], "train",
                                         num_negatives=config['num_negatives'])

            best = evaluate(sess, model, "valid", valid_manager, logger, id2mid, id2p)
            if best:
                save_model(sess, model, config['ckpt_path'], logger)
                all_precision, all_ndcg, all_map, all_ndcg_topall = evaluate(sess, model, "test", test_manager, logger, id2mid, id2p)
                print("new best test precision at {} :{:>.5f}".format(k, all_precision))
                print("new best test ndcg at {} :{:>.5f}".format(k, all_ndcg))
                print("new best test map :{:>.5f}".format(all_map))
                print("test best test ndcg_topall :{:>.5f}".format(all_ndcg_topall))

        best_dev_precision = model.best_dev_precision.eval()
        best_dev_ndcg = model.best_dev_ndcg.eval()
        print("final best dev precision at {} :{:>.5f}".format(k, best_dev_precision))
        print("final best dev ndcg at {} :{:>.5f}".format(k, best_dev_ndcg))

        best_test_precision = model.best_test_precision.eval()
        best_test_ndcg = model.best_test_ndcg.eval()
        print("final best test precision at {} :{:>.5f}".format(k, best_test_precision))
        print("final best test ndcg at {} :{:>.5f}".format(k, best_test_ndcg))


def __test():
    """
    test interface
    :return:
    """
    log_path = os.path.join(".", config['log_path'])
    config['ckpt_path'] = "Pretrain/" + domain + "/"
    logger = get_logger(log_path)
    id2mid, id2p, entity_entity_sim_Matrix, entity_relation_Adj, truth_label, train_entity_list, \
    test_entity_list, valid_entity_list = data_reader(logger, config=config, domain=domain,
                                                      entity_entity_topk=entity_knn_number)
    test_data = (entity_relation_Adj, entity_entity_sim_Matrix, test_entity_list, truth_label)
    test_manager = BatchManager(test_data, len(test_entity_list), "test")

    tf_config = tf.ConfigProto()
    tf_config.gpu_options.allow_growth = True

    with tf.Session(config=tf_config) as sess:
        model = create_model(sess, Model, config['ckpt_path'], config, logger)
        logger.info("start test")
        test_precision, test_ndcg, test_map, test_ndcg_topall = evaluate(sess, model, "test", test_manager, logger,
                                                                         id2mid, id2p)
        print("test precision at {} :{:>.5f}".format(k, test_precision))
        print("test ndcg at {} :{:>.5f}".format(k, test_ndcg))
        print("test map :{:>.5f}".format(test_map))
        print("test ndcg_topall :{:>.5f}".format(test_ndcg_topall))


def prediction():
    """
    prediction interface
    :return:
    """
    print("prediction")
    log_path = os.path.join(".", config['log_path'])
    logger = get_logger(log_path)
    map_file_path = "./pkl/" + domain + "/data.pkl"
    if os.path.isfile(map_file_path):
        with open(map_file_path, "rb") as f:
            id2mid, id2p, entity_entity_sim_Matrix, entity_relation_Adj, truth_label, \
            train_entity_list, test_entity_list, valid_entity_list, entity_size, relation_size = pickle.load(f)
            config['entity_size'] = entity_size
            config['relation_size'] = relation_size
    else:
        id2mid, id2p, entity_entity_sim_Matrix, entity_relation_Adj, truth_label, train_entity_list, \
        test_entity_list, valid_entity_list = data_reader(logger, config=config, domain=domain,
                                                          entity_entity_topk=entity_knn_number)
        with open(map_file_path, "wb") as f:
            pickle.dump([id2mid, id2p, entity_entity_sim_Matrix, entity_relation_Adj, truth_label,
                         train_entity_list, test_entity_list, valid_entity_list, config['entity_size'],
                         config['relation_size']], f)
    test_data = (entity_relation_Adj, entity_entity_sim_Matrix, test_entity_list, truth_label)
    test_manager = BatchManager(test_data, config['batch_size'], "test")

    tf_config = tf.ConfigProto()
    tf_config.gpu_options.allow_growth = True
    config['decay_steps'] = 100
    config['ckpt_path'] = "result/" + datasettype + "/" + domain + "/"
    rec_results = {}
    with tf.Session(config=tf_config) as sess:
        model = create_model(sess, Model, config['ckpt_path'], config, logger)
        for batch in test_manager.iter_batch():  # each batch is an entity
            entity_relation_Adj, entity_entity_sim_Matrix, input_entity, input_relation, label = batch
            relation_z = model.run_step(sess, False, batch)
            relation_z = relation_z[0]

            reserved_prop_list = []
            for p in range(len(entity_relation_Adj[input_entity[0]])):
                if entity_relation_Adj[input_entity[0]][p] >= 1:
                    reserved_prop_list.append(p)

            entityid = input_entity[0]
            for index in range(len(input_entity)):  # each batch is an entity
                # entityid = input_entity[index]
                relationid = input_relation[index]
                if entity_relation_Adj[entityid][relationid] >= 1:  # delete pre-reserved properties
                    relation_z[index] = -2.0

            map_property_score = {key: value for key, value in enumerate(relation_z)}
            exps_list = heapq.nlargest(k, map_property_score, key=map_property_score.get)

            rec_results[id2mid[entityid]] = [id2p[pid] for pid in exps_list]
    return rec_results


def evaluate(sess, model, name, data, logger, id2mid, id2p):
    logger.info("evaluate data:{}".format(name))
    all_precision = 0
    all_ndcg = 0
    test_entity_cnt = 0
    all_map = 0
    all_ndcg_topall = 0

    for batch in data.iter_batch():
        entity_relation_Adj, entity_entity_sim_Matrix, input_entity, input_relation, label = batch
        relation_z = model.run_step(sess, False, batch)  # batch_size*1
        relation_z = relation_z[0]

        test_entity_cnt += 1  # each batch is an entity
        reserved_prop_list = []
        for p in range(len(entity_relation_Adj[input_entity[0]])):
            if entity_relation_Adj[input_entity[0]][p] >= 1:
                reserved_prop_list.append(p)

        for index in range(len(input_entity)):
            entityid = input_entity[index]
            relationid = input_relation[index]
            if entity_relation_Adj[entityid][relationid] >= 1:
                relation_z[index] = -2.0  # exclude pre-reserved properties

        truth = label
        truth_set = set()
        for a in range(len(truth)):
            if truth[a] == 1 and a not in reserved_prop_list:  # exclude pre-reserved properties
                truth_set.add(a)

        map_property_score = {key: value for key, value in enumerate(relation_z)}
        exps_list = heapq.nlargest(k, map_property_score, key=map_property_score.get)
        precision = precision_at_k(exps_list, truth_set, k)
        ndcg = ndcg_at_k(exps_list, truth_set, k)

        ranklist_topall = sorted(map_property_score.items(), key=lambda d: d[1], reverse=True)
        ranklist_topall = [key for key, value in ranklist_topall]
        map_topall = ap(ranklist_topall, truth_set)
        ndcg_topall = ndcg_at_k(ranklist_topall, truth_set, 10000)

        all_precision += precision
        all_ndcg += ndcg
        all_map += map_topall
        all_ndcg_topall += ndcg_topall

    all_precision = all_precision / test_entity_cnt
    all_ndcg = all_ndcg / test_entity_cnt
    all_map = all_map / test_entity_cnt
    all_ndcg_topall = all_ndcg_topall / test_entity_cnt

    if name == "valid":
        best_dev_precision = model.best_dev_precision.eval()
        best_dev_ndcg = model.best_dev_ndcg.eval()
        if all_precision >= best_dev_precision and all_ndcg >= best_dev_ndcg:
            tf.assign(model.best_dev_precision, all_precision).eval()
            tf.assign(model.best_dev_ndcg, all_ndcg).eval()
            logger.info("new best dev precision at {} :{:>.5f}".format(k, all_precision))
            logger.info("new best dev ndcg at {} :{:>.5f}".format(k, all_ndcg))
        return all_precision > best_dev_precision and all_ndcg > best_dev_ndcg

    elif name == "test":
        best_test_precision = model.best_test_precision.eval()
        best_test_ndcg = model.best_test_ndcg.eval()
        if all_precision >= best_test_precision and all_ndcg >= best_test_ndcg:
            tf.assign(model.best_test_precision, all_precision).eval()
            tf.assign(model.best_test_ndcg, all_ndcg).eval()
            logger.info("new best test precision at {} :{:>.5f}".format(k, all_precision))
            logger.info("new best test ndcg at {} :{:>.5f}".format(k, all_ndcg))

        return all_precision, all_ndcg, all_map, all_ndcg_topall


if __name__ == "__main__":
    if mode == "train":
        train()
    elif mode == "test":
        if simulate:
            __test()
        else:
            rec_results = prediction()
            path = os.path.join(".", "result/" + datasettype + "/")
            if not os.path.exists(path):
                os.makedirs(path)
            tail_entity_property_rec_file = "." + datasettype + ".test.prop.rec.txt"
            with open(path + domain + tail_entity_property_rec_file, "w") as f:
                for key, value in rec_results.items():
                    f.write(key + "\t" + ",".join(str(prop) for prop in value) + "\n")
