FROM java:openjdk-8u91-jdk

RUN mkdir -p /usr/local/zookeeper-conf
RUN mkdir -p /usr/local/zookeeper-data
RUN (wget -q -O - https://archive.apache.org/dist/zookeeper/zookeeper-3.5.6/apache-zookeeper-3.5.6-bin.tar.gz | tar -xzf - -C /usr/local)
COPY zoo.cfg /usr/local/apache-zookeeper-3.5.6-bin/conf/

EXPOSE 2181 2888 3888

WORKDIR /usr/local/zookeeper-3.5.6

VOLUME ["/usr/local/apache-zookeeper-3.5.6-bin/conf", "/usr/local/zookeeper-data", "/usr/local/zookeeper-conf"]

ENTRYPOINT ["/usr/local/apache-zookeeper-3.5.6-bin/bin/zkServer.sh"]
CMD ["start-foreground"]

