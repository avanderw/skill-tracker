#!/bin/sh
fullstamp=$(date +"%Y-%m-%d_%H-%M-%S")

if [ ! -d backups ]
then
    mkdir "backups"
fi

cp skill-tracker.sqlite backups/skill-tracker.sqlite-"$fullstamp"