import numpy as np


def precision_at_k(exps, reals, k=10):
    size = k if len(exps) > k else len(exps)
    hits = [exps[i] in reals for i in range(size)]
    return np.divide(np.sum(hits), k)


def ndcg_at_k(exps, reals, k=10):
    ndcg = 0
    size = k if len(exps) > k else len(exps)
    hits = [exps[i] in reals for i in range(size)]
    dcg = np.sum([np.divide(hits[i], np.log2(i+2)) for i in range(size)])
    size = k if len(reals) > k else len(reals)
    idcg = np.sum([np.divide(1, np.log2(i+2)) for i in range(size)])
    if idcg:
        return np.divide(dcg, idcg)
    else:
        return ndcg


def ap(exps, reals):
    hits = 0
    sum_precs = 0
    for n in range(len(exps)):
        if exps[n] in reals:
            hits += 1
            sum_precs += hits / (n + 1.0)
    if hits > 0:
        return sum_precs / len(reals)
    else:
        return 0
