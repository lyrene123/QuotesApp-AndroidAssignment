#!/bin/bash
#
# p.m.campbell
#
# 2017-09-24
# create multiple repos in an organization on github.com
#
# see https://developer.github.com/v3/repos/#create
# api for user: https://api.github.com/user/repos

# TODO find a way to give password properly instead of each time

echo $(basename $0)
result="result.$(date +%F)"
echo > $result

read -p "enter your github userid " uid
read -p "enter your github organization " org
read -p "enter your repo basename " repo
#read -p "enter \# repos you want " count
read -p "enter description for repo " desc

for i in {17..18} ; do
#for i in {1..$count} ; do
#for i in {3 ; do
  repos=$repo$i
  echo -e "Repo $repos" >> $result
  curl -u $uid  https://api.github.com/orgs/$org/repos \
   --request POST \
   -d "{\"name\":\"$repos\", \"description\":\"$desc\", \"private\":\"true\"}" \
   >> $result
  echo -e "================" >> $result
done

echo results are in $result
