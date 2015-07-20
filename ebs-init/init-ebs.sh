#!/bin/bash

echo ""
echo "Creating directory <volume>/data"
mkdir /volume/ebs/data

echo ""
echo "Changing permissions of <volume>/data"
chmod 0700 /volume/ebs/data

echo ""
echo "Listing <volume>/"
ls -al /volume/ebs

echo ""
echo "Listing <volume>/data"
ls -al /volume/ebs/data

echo ""
echo "Setup complete"

# wait forever so the container/image does not get restarted in kubernetes
tail -f /dev/null