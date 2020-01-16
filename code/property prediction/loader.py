import math
import random
from collections import OrderedDict

import numpy as np
import scipy.sparse as sp
import sys

from utils import get_logger

sys.path.append(".")


def config_model():
    config = OrderedDict()
    return config


def get_sim_by_property(L):
    """
    :param L: L = np.zeros((eid, pid), dtype=np.float32)
    :return:
    """
    sim_property = {}
    num_entity, num_property = L.shape
    for eid1 in range(num_entity):
        for eid2 in range(num_entity):
            if eid1 < eid2:
                p_1 = L[eid1]
                p_2 = L[eid2]
                same_count = sum([min(p_1[x], p_2[x]) for x in range(num_property)])
                if eid1 not in sim_property.keys():
                    sim_property[eid1] = {}
                if eid2 not in sim_property.keys():
                    sim_property[eid2] = {}
                sim_property[eid1][eid2] = same_count / sum(p_1)
                sim_property[eid2][eid1] = same_count / sum(p_2)

    return sim_property


def get_entity_sim_Matrix(S, sim_property, entity_entity_topk, train_entity_length):
    """

    :param S: type\pagerank\value similar score np.zeros((eid, eid, 3), dtype=np.float32)
    :param sim_property: score based on similar property {eid1:{eid2:score,eid3:score}}
    :param entity_entity_topk:
    :param train_entity_length:
    :return: entity_entity_sim_Matrix np.zeros((eid,eid), dtype=np.float32)
    """
    entity_entity_Matrix = np.zeros((S.shape[0], S.shape[0]), np.float32)
    for eid0 in range(S.shape[0]):
        entity_sim = {}
        for eid1 in range(train_entity_length):
            if eid0 == eid1:
                continue
            sim_merge = S[eid0][eid1][0] + S[eid0][eid1][2] + sim_property[eid0][eid1]
            entity_sim[eid1] = sim_merge
        sorted_entity_list = sorted(entity_sim.items(), key=lambda d: d[1], reverse=True)
        for i in range(min(entity_entity_topk, len(sorted_entity_list))):
            eid1 = sorted_entity_list[i][0]
            entity_entity_Matrix[eid0][eid1] = 1

    return entity_entity_Matrix


def get_relation_sim_Matrix(sim_entity, relaiton_len, relation_relation_topk):
    relation_relation_Matrix = np.zeros((relaiton_len, relaiton_len), np.float32)
    for pid1 in range(relaiton_len):
        sorted_relation_list = sorted(sim_entity[pid1].items(), key=lambda d: d[1], reverse=True)

        for i in range(min(relation_relation_topk, len(sorted_relation_list))):
            pid2 = sorted_relation_list[i][0]
            relation_relation_Matrix[pid1][pid2] = 1

    return relation_relation_Matrix


def load_entities(eid, mid2id, id2mid, filepath):
    entity_list = []
    with open(filepath) as f:
        for line in f.readlines():
            line = line.strip("\n")
            if line not in mid2id.keys():
                mid2id[line] = eid
                id2mid[eid] = line
                entity_list.append(eid)
                eid += 1
            else:
                print(line + " repeat with training entity")
    return entity_list, eid


def load_properties(pid, p2id, id2p, property_list, filepath):
    with open(filepath) as f:
        for line in f.readlines():
            items = line.strip("\n").split("\t")
            if items[1] not in p2id:
                p2id[items[1]] = pid
                id2p[pid] = items[1]
                property_list.append(pid)
                pid += 1
    return pid


def load_similarity_matrix(S, mid2id, filepath, maxs1, maxs2, maxs3):
    with open(filepath) as f:
        for line in f.readlines():
            items = line.strip("\n").split("\t")
            id1 = mid2id[items[0]]
            id2 = mid2id[items[1]]
            if float(items[2]) > maxs1:
                maxs1 = float(items[2])
            if float(items[3]) > maxs2:
                maxs2 = float(items[3])
            if float(items[4]) > maxs3:
                maxs3 = float(items[4])
            S[id1][id2] = np.asarray([items[2], items[3], items[4]])
            S[id2][id1] = np.asarray([items[2], items[3], items[4]])
    return maxs1, maxs2, maxs3


def load_adj_matrix(entity_relation_Adj, mid2id, p2id, filepath):
    with open(filepath) as f:
        for line in f.readlines():
            items = line.strip("\n").split("\t")
            if items[1] not in p2id.keys():
                print(items[1] + "not in train data")
                continue
            entity_relation_Adj[mid2id[items[0]]][p2id[items[1]]] = 1


def data_reader(logger, config, domain="film.film", entity_entity_topk=50):
    """
    :param logger:
    :param config:
    :param domain:
    :param entity_entity_topk:
    :return:
    """
    path = "./data/"
    mid2id = {}
    id2mid = {}
    eid = 0

    simulate = config["simulate"]
    if simulate:
        test_entity_file = ".test.instances.txt"
        test_entity_property_file = ".test.ground.truth.reserved.txt"
        test_entity_similarity_file = ".test.similarities.txt"
    else:
        test_entity_file = ".real-world.test.instances.txt"
        test_entity_property_file = ".real-world.test.ground.truth.txt"
        test_entity_similarity_file = ".real-world.test.similarities.txt"

    train_entity_list, eid = load_entities(eid, mid2id, id2mid, path + domain + "/" + domain + ".train.instances.txt")
    test_entity_list, eid = load_entities(eid, mid2id, id2mid, path + domain + "/" + domain + test_entity_file)
    valid_entity_list, eid = load_entities(eid, mid2id, id2mid, path + domain + "/" + domain + ".valid.instances.txt")
    # print(id2mid)

    # Entity similarity matrix
    S = np.zeros((eid, eid, 3), dtype=np.float32)
    maxs1, maxs2, maxs3 = 0, 0, 0
    maxs1, maxs2, maxs3 = load_similarity_matrix(S, mid2id,
                                                 path + domain + "/" + domain + ".train.similarities.txt",
                                                 maxs1, maxs2, maxs3)
    maxs1, maxs2, maxs3 = load_similarity_matrix(S, mid2id,
                                                 path + domain + "/" + domain + test_entity_similarity_file,
                                                 maxs1, maxs2, maxs3)
    maxs1, maxs2, maxs3 = load_similarity_matrix(S, mid2id,
                                                 path + domain + "/" + domain + ".valid.similarities.txt",
                                                 maxs1, maxs2, maxs3)
    # Normalized
    for i in range(len(S)):
        for j in range(len(S[0])):
            S[i][j][0] = S[i][j][0] / maxs1
            S[i][j][1] = S[i][j][1] / maxs2
            S[i][j][2] = S[i][j][2] / maxs3

    pid = 0
    p2id = {}
    id2p = {}
    property_list = []

    pid = load_properties(pid, p2id, id2p, property_list,
                          path + domain + "/" + domain + ".train.ground.truth.txt")
    pid = load_properties(pid, p2id, id2p, property_list,
                          path + domain + "/" + domain + test_entity_property_file)
    pid = load_properties(pid, p2id, id2p, property_list,
                          path + domain + "/" + domain + ".valid.ground.truth.reserved.txt")

    logger.info("property count :{}".format(pid))
    logger.info("entity count:{}".format(eid))

    config['entity_size'] = eid
    config['relation_size'] = pid

    entity_relation_Adj = np.zeros((eid, pid), dtype=np.float32)
    load_adj_matrix(entity_relation_Adj, mid2id, p2id,
                    path + domain + "/" + domain + ".train.ground.truth.txt")
    # read test entity reserved property
    load_adj_matrix(entity_relation_Adj, mid2id, p2id,
                    path + domain + "/" + domain + test_entity_property_file)
    # read valid entity reserved property
    load_adj_matrix(entity_relation_Adj, mid2id, p2id,
                    path + domain + "/" + domain + ".valid.ground.truth.reserved.txt")

    # Calculate the entities similarity based the existing attributes of the entity
    sim_property = get_sim_by_property(entity_relation_Adj)

    # Constructing an entity_entity similarity matrix
    entity_entity_sim_Matrix = get_entity_sim_Matrix(S, sim_property, entity_entity_topk, len(train_entity_list))

    # load all properties
    truth_label = np.zeros((eid, pid), dtype=np.float32)
    if simulate:
        load_adj_matrix(truth_label, mid2id, p2id,
                        path + domain + "/" + domain + ".test.ground.truth.txt")
    load_adj_matrix(truth_label, mid2id, p2id,
                    path + domain + "/" + domain + ".valid.ground.truth.txt")

    return id2mid, id2p, entity_entity_sim_Matrix, entity_relation_Adj, \
           truth_label, train_entity_list, test_entity_list, valid_entity_list


class BatchManager(object):

    def __init__(self, data, batch_size, type, num_negatives=4):
        entity_input, relation_input, labels = [], [], []
        if type == "train":
            entity_relation_Adj, entity_entity_sim_Matrix, entity_list = data
            num_entity = entity_relation_Adj.shape[0]
            num_relation = entity_relation_Adj.shape[1]

            trainMatrix = sp.dok_matrix((num_entity, num_relation), dtype=np.float32)
            for i in range(len(entity_relation_Adj)):
                for j in range(len(entity_relation_Adj[0])):
                    if entity_relation_Adj[i][j] == 1:
                        trainMatrix[i, j] = 1.0

            # generate positive and negative instances
            for (u, i) in trainMatrix.keys():
                # positive instance
                entity_input.append(u)
                relation_input.append(i)
                labels.append(1)
                # negative instances
                for t in range(num_negatives):
                    j = np.random.randint(num_relation)
                    while (u, j) in trainMatrix:
                        j = np.random.randint(num_relation)
                    entity_input.append(u)
                    relation_input.append(j)
                    labels.append(0)

            num_batch = int(math.ceil(len(entity_input) / batch_size))
            self.batch_data = list()

            for i in range(num_batch):
                input_entity = entity_input[i * batch_size: (i + 1) * batch_size]
                input_relation = relation_input[i * batch_size: (i + 1) * batch_size]
                label = labels[i * batch_size: (i + 1) * batch_size]
                self.batch_data.append(
                    [entity_relation_Adj, entity_entity_sim_Matrix, input_entity, input_relation, label])

        elif type == "valid" or type == "test":  # Evaluate an entity and all its properties one time
            entity_relation_Adj, entity_entity_sim_Matrix, entity_list, truth_label = data
            num_entity = entity_relation_Adj.shape[0]
            num_relation = entity_relation_Adj.shape[1]
            num_batch = len(entity_list)
            self.batch_data = list()
            for u in entity_list:
                for j in range(len(entity_relation_Adj[0])):
                    entity_input.append(u)
                    relation_input.append(j)
                    labels.append(truth_label[u][j])  # truth label
            for i in range(num_batch):
                input_entity = entity_input[i * num_relation: (i + 1) * num_relation]
                input_relation = relation_input[i * num_relation: (i + 1) * num_relation]
                label = labels[i * num_relation: (i + 1) * num_relation]
                self.batch_data.append(
                    [entity_relation_Adj, entity_entity_sim_Matrix, input_entity, input_relation, label])

        self.len_data = len(self.batch_data)

    def iter_batch(self, shuffle=False):
        """
        :param shuffle:
        :return:
        """
        if shuffle:
            random.shuffle(self.batch_data)
        for idx in range(self.len_data):
            yield self.batch_data[idx]


if __name__ == "__main__":
    config = config_model()
    config["simulate"] = False
    logger = get_logger("./temp")
    data_reader(logger, config, "film.actor")
