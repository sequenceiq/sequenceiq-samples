#!/bin/bash -e

[[ "$TRACE" ]] && set -x

################# CHECK VARIABLES #################
  NODES_IPS='146.148.44.172 146.148.76.27 146.148.93.207 130.211.113.239 173.255.112.171'
  PRIVATE_KEY_PATH=${YOUR_IDENTITY_FILE_FOR_THE_NODES_SSH}
  P12_KEY_FILE=${YOUR_KEY_FILE}
################# CHECK VARIABLES #################

main() {
  for actualip in ${NODES_IPS}
  do
    cpResources $actualip
  done
}

cpResources() {
  scp -o StrictHostKeyChecking=no \
      -o UserKnownHostsFile=/dev/null \
      -o LogLevel=QUIET \
      -i $PRIVATE_KEY_PATH \
  $P12_KEY_FILE ubuntu@$actualip:/tmp/$P12_KEY_FILE

  scp -o StrictHostKeyChecking=no \
      -o UserKnownHostsFile=/dev/null \
      -o LogLevel=QUIET \
      -i $PRIVATE_KEY_PATH \
    copyscripts.sh ubuntu@$actualip:/tmp/copyscripts.sh

ssh -o StrictHostKeyChecking=no \
    -o UserKnownHostsFile=/dev/null \
    -o LogLevel=QUIET \
    -i $PRIVATE_KEY_PATH \
  ubuntu@$actualip "chmod +x /tmp/copyscripts.sh && sudo /tmp/copyscripts.sh"

}

main
