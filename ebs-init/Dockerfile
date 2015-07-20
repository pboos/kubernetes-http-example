FROM    postgres

COPY init-ebs.sh /

RUN chmod +x /init-ebs.sh

VOLUME /volume/ebs

CMD ["./init-ebs.sh"]