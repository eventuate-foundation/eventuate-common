FROM amazoncorretto:17.0.14-al2023

RUN mkdir -p /usr/local/zookeeper-conf /usr/local/zookeeper-data && \
  yum install -y wget tar gzip nc && \
  (wget -q -O - https://archive.apache.org/dist/zookeeper/zookeeper-3.5.6/apache-zookeeper-3.5.6-bin.tar.gz | tar -xzf - -C /usr/local) && \
    yum clean all && \
    rm -rf /var/cache/yum

COPY zoo.cfg /usr/local/apache-zookeeper-3.5.6-bin/conf/

EXPOSE 2181 2888 3888

WORKDIR /usr/local/apache-zookeeper-3.5.6-bin

VOLUME ["/usr/local/apache-zookeeper-3.5.6-bin/conf", "/usr/local/zookeeper-data", "/usr/local/zookeeper-conf"]

COPY run-zk.sh .
ENTRYPOINT ["./run-zk.sh"]
CMD ["start-foreground"]
HEALTHCHECK --interval=3s --retries=30 CMD (echo ruok | nc localhost 2181) || exit 1
