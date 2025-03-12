	#!/bin/sh 

echo "STARTING"

#if no args: raise error
if [ $# -eq 0 ]
    then
    echo "ERROR: No arguments supplied"
    echo "E.g."
    echo "./install.sh local "
    exit
fi

if [ "$1" == "clean" ]; then
    echo "Clear requested"
    mvn clean
    exit
fi

if [ "$1" == "local" ]; then
    echo "Local installaion"
    mvn -U install
    exit
fi

if [ "$1" == "nexus" ]; then
    echo "Deployment to nexus"
    mvn -U clean deploy
    exit
fi

echo "No correct input argument specified."
echo "'clean', 'local' and 'nexus' are the available options" 
