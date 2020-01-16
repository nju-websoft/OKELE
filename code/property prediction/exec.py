import os
import sys
train_args = {
              "boats.ship": {"entity_knn_number": 100, "epochs": 100},
              "film.actor": {"entity_knn_number": 100, "epochs": 100},
              "film.film": {"entity_knn_number": 100, "epochs": 100},
              "architecture.building": {"entity_knn_number": 100, "epochs": 100},
              "computer.software": {"entity_knn_number": 100, "epochs": 100},
              "geography.mountain": {"entity_knn_number": 100, "epochs": 100},
              "medicine.drug": {"entity_knn_number": 100, "epochs": 100},
              "book.book": {"entity_knn_number": 100, "epochs": 100},
              "music.album": {"entity_knn_number": 100, "epochs": 100},
              "food.food": {"entity_knn_number": 100, "epochs": 100}
}

devices = sys.argv[1]
attention_flag = sys.argv[2]
os.system("mkdir log")
os.system("mkdir ckpt")
os.system("mkdir pkl")
for dataset in train_args.keys():
    args = train_args[dataset]
    os.system("mkdir ckpt/" + dataset)
    os.system("mkdir pkl/" + dataset)
    os.system("rm -r pkl/" + dataset + "/*")
    for i in range(1):
        os.system("rm -r ckpt/" + dataset + "/*")  # clean old model
        if devices != '-1':
            os.system(
                "CUDA_VISIBLE_DEVICES=" + str(devices) + " python main.py --domain=" + str(dataset)
                + " --entity_knn_number=" + str(args["entity_knn_number"])
                + " --epochs=" + str(args["epochs"])
                + " --attention_flag=" + attention_flag + " --mode=train")
        else:
            os.system(
                "python main.py --domain=" + str(dataset)
                + " --entity_knn_number=" + str(args["entity_knn_number"])
                + " --epochs=" + str(args["epochs"])
                + " --attention_flag=" + attention_flag + " --mode=train")
