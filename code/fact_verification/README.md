## Overview
This is the source code and experimental data for fact verification, which is a probabilistic graphical model.

## Datasets ([GoogleDrive](https://drive.google.com/open?id=1ILH5e13O5D6JDzZK1Nlhh0wCbdH8UDxP))
We apply the algorithms on both synthetic and real-world datasets. The following is the guide about data files:

- `synthetic_data/:` Raw value collection files and gold standard used in the synthetic experiment.
- `real-world_data/:` Raw value collection files and gold standard used in the real-world experiment. For each long-tail entity, properties are predicted with the property prediction model.
- `prior_data_synthetic/:` Prior data used in the synthetic experiment.
- `prior_data_real-world/:` Prior data used in the real-world experiment.

## Models
The models that have been implemented are as follows, including 8 start-of-the-art comparative models and OKELE.

- `Majority voting:` regards the fact with the maximum number of occurrences as truth.
- `TruthFinder:` Xiaoxin Yin, Jiawei Han, and Philip S. Yu. 2008. Truth Discovery with Multiple Conﬂicting Information Providers on the Web. IEEE Transactions on Knowledge and Data Engineering 20, 6 (2008), 796–808.
- `PooledInvestment:` Jeﬀ Pasternack and Dan Roth. 2010. Knowing What to Believe (when you already know something). In COLING. ACL, Beijing, China, 877–885.
- `LTM:` Bo Zhao, Benjamin I. P. Rubinstein, Jim Gemmell, and Jiawei Han. 2012. A Bayesian Approach to Discovering Truth from Conﬂicting Sources for Data Integration. Proceedings of the VLDB Endowment 5, 6 (2012), 550–561.
- `LCA:` Jeﬀ Pasternack and Dan Roth. 2013. Latent Credibility Analysis. In WWW. IW3C2, Rio de Janeiro, Brazil, 1009–1020.
- `CATD:` Qi Li, Yaliang Li, Jing Gao, Lu Su, Bo Zhao, Murat Demirbas, Wei Fan, and Jiawei Han. 2014. A Confdence-Aware Approach for Truth Discovery on Long-Tail Data. Proceedings of the VLDB Endowment 8, 4 (2014), 425–436.
- `MBM:` Xianzhi Wang, Quan Z. Sheng, Xiu Susie Fang, Lina Yao, Xiaofei Xu, and Xue Li. 2015. An Integrated Bayesian Approach for Eﬀective Multi-Truth Discovery. In CIKM. ACM, Melbourne, Australia, 493–502.
- `BWA:` Yuan Li, Benjamin I. P. Rubinstein, and Trevor Cohn. 2019. Truth Inference at Scale: A Bayesian Model for Adjudicating Highly Redundant Crowd Annotations. In WWW. IW3C2, San Francisco, CA, USA, 1028–1038.
- `OKELE:` The fact verification algorithm proposed in OKELE.

## Usage
Import the project as a Maven project, then build it normally (recommended). Alternatively, import as Java project and set the classpath manually to include all dependencies listed in pom.xml. Make sure you have installed 
**Java 8** and **Maven** on your computer first.
