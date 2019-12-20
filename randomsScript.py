import random

def createSeeds():
    seeds = [] 
    seed = 1 
    for i in range (1,1001):
        while True:
            if seed in seeds:
                seed = random.randint(1, 100000)
            else:
                seeds.append(seed)
                break

    return seeds

file = open('seeds.txt', 'w')

seeds = createSeeds()

for seed in seeds:
    file.write(str(seed))
    file.write('\n')





    
