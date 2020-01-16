## Overview
This is the source code and experimental data for property prediction, which is an attention-based GNN model.

## Requirements
  - Python (>=3.6)
  - Numpy (>=1.13.3)
  - TensorFlow (>=1.12)
  - keras (>=2.2.4)

## Provided Data([GoogleDrive](https://drive.google.com/open?id=1DVYmXvUnT0a-474AdpFZKaSbdMEIJFq1))
This project provides data from 10 classes, including `architecture.building`, `boats.ship`, `book.book`, `computer.software`, `film.actor`, `film.film`, `food.food`, `geography.mountain`, `medicine.drug` and `music.album`. 

The original entity-property data are in the `data` directory, where the file suffix `.instances.txt` is the entity file, `.ground.truth.txt` is the ground truth file of all properties owned by entities, `.ground.truth.reserved.txt` is a file of reserved properties for property prediction and `.similarities.txt` is a file of similarity scores between entities.

The pre-trained models are in the `Pretrain` directory.

## Train & Test
The default hyper-parameters are showed in the paper.

1.**Train**: Run `exec.py {gpu_devices} {atten_flag}`. For example, if you want to train model with graph attention mechanism on GPU card 1, run the following command

	python exec.py 1 1
	

For `gpu_devices`, -1 means not using GPU. For `atten_flag`, 0 means not using graph attention mechanism, 1 is the opposite.

2.**Test**: Run `python main.py --domain={classes_name} --entity_knn_number={entity_knn_number} --epochs={epochs} --attention_flag={atten_flag} --mode=test --simulate=1`. For example, if you want to test model on class `boats.ship`, run the following command

	python main.py --domain=boats.ship --entity_knn_number=100 --epochs=100 --attention_flag=1 --mode=test --simulate=1

3.**Prediction**: Run `python main.py --domain={classes_name} --entity_knn_number={entity_knn_number} --epochs={epochs} --attention_flag={atten_flag} --mode=test --simulate=0 `. For example, if you want to prediction model on class `boats.ship`, run the following command

	python main.py --domain=boats.ship --entity_knn_number=100 --epochs=100 --attention_flag=1 --mode=test --simulate=0

The prediction result of `Real-World Experiment` is in `result/real-world/*` directory.