#! /bin/bash -e

if [[ -f /usr/src/app/additional-scripts/initialization-completed ]] ; then
  exit 0
fi

exit 1
