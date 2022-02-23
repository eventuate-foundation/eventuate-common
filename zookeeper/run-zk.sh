#! /usr/bin/bash -e


export SERVER_JVMFLAGS="$KAFKA_OPTS -Dzookeeper.4lw.commands.whitelist=ruok"

/usr/local/apache-zookeeper-3.5.6-bin/bin/zkServer.sh $*
