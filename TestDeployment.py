import subprocess
from multiprocessing.dummy import Pool as ThreadPool
import codecs
import argparse
import platform
import random
import shutil

# Arguments for the ic20 tool
ARGUMENTS = []
# Seeds to play
SEEDS = []
# URL to connect to
URL = "http://localhost:50123"
# URL to AWS
AWS_URL = "https://udi8pt9vo9.execute-api.us-east-1.amazonaws.com/Beta/"
# Number of threads running at the same time
N_WORKERS = 4

# Location of executable
PATH = './ic20_linux'

# Parse arguments given to script
parser = argparse.ArgumentParser(description = 'Test the winrate of a server.')
parser.add_argument('-aws', action = 'store_true', help = 'If the flag is set AWS is tested, else localhost.')
parser.add_argument('-train', action = 'store_true', help = 'If the flag is set the constants file is updated after each iteration.')
parser.add_argument('-file', action = 'store_true', help = 'If the flag is set the seeds.txt file is used. Overrides range.')
parser.add_argument('--threads', default = 4, type = int, help = 'Sets the amout of threads.')
parser.add_argument('--range', default = [1, 100], nargs = 2, type = int, help = 'Sets the range of seeds to play.')
args = parser.parse_args()

# Set URL to AWS URL
if(args.aws):
    URL = AWS_URL


# Set seeds
SEEDS = list(range(args.range[0], args.range[1]))

# Parse seeds out of seeds file
if(args.file):
    with codecs.open('seeds.txt', mode='r', encoding='utf_8') as f:
        for line in f.read().split("\n"):
            if(line != ""):
                SEEDS.append(int(line))

# Set number of worker threads
N_WORKERS = args.threads

# Update path for windows
if(platform.system() == 'Windows'):
    PATH = 'ic20_windows.exe'

# Set full arguments list
ARGUMENTS = [PATH, '-u', URL, '-t', '0']


wins = 0
loss = 0

CHANGE_CHANCE = 0.25
MAX_ADDITION = 5
MAX_PERCENTAGE_CHANCE = 0.5

def updateConstants():
    global CHANGE_CHANCE
    global MAX_ADDITION
    global MAX_PERCENTAGE_CHANCE

    with codecs.open('constants.txt', mode='r', encoding='utf_8') as f:
        newConstants = ""
        for line in f.read().split("\n"):
            line = line.strip()
            # Skip lines that are either empty, constants or comments
            if(line == "" or line.startswith("#") or line.startswith("//")):
                newConstants += line + "\n"
                continue

            # Adjust variables
            key = line.split(" ")[0]
            value = float(line.split(" ")[1])

            if(random.random() < CHANGE_CHANCE):
                value += MAX_ADDITION * (random.random() * 2 - 1)
                value *= MAX_PERCENTAGE_CHANCE * (random.random() * 2 - 1) + 1

                value = value if value > 0 else 0

            newConstants += key + " " + str(value) + "\n"

    with codecs.open('constants.txt', mode='w', encoding='utf_8') as f:
        f.write(newConstants)
    

def calculateWinrate(outcomes):
    wins = 0
    games = 0
    for outcome in outcomes:
        # Prevent incomplete data to mess with the updates
        if(outcome == None):
            return -1

        if(outcome[1] == "win"):
            wins += 1

        games += 1
    
    return wins / games
        



def playGame(seed):
    global wins
    global loss
    global ARGUMENTS
    args = ARGUMENTS[:]
    args.extend(['-s', str(seed)])
    result = subprocess.run(args, stdout=subprocess.PIPE).stdout.decode('utf-8')
    if("win" in result):
        wins += 1   
        #print("Game: %d    Seed: %d    Outcome: Win" % ((wins + loss), seed))
        #print("Winrate %f%%" % (wins * 100 / (wins + loss)))
        return (seed, "win")
    elif("loss" in result):
        loss += 1 
        #print("Game: %d    Seed: %d    Outcome: Loss" % ((wins + loss), seed))
        #print("Winrate %f%%" % (wins * 100 / (wins + loss)))
        return (seed, "loss")


# Make the Pool of workers
pool = ThreadPool(N_WORKERS)

# Open the URLs in their own threads
# and return the results
results = pool.map(playGame, SEEDS)
print(results)
print(calculateWinrate(results))
loss = 0
wins = 0

if(args.train):
    oldWinrate = calculateWinrate(results)
    while(1):
        shutil.copy('constants.txt', 'oldConstants.txt')
        updateConstants()
        results = pool.map(playGame, SEEDS)
        print(results)
        print(calculateWinrate(results))
        loss = 0
        wins = 0
        if(calculateWinrate(results) > oldWinrate):
            # Save new best winrate
            oldWinrate = calculateWinrate(results)
        else:
            # Restore better constants
            shutil.copy('oldConstants.txt', 'constants.txt')


# Close the pool and wait for the work to finish
pool.close()
pool.join()


updateConstants()