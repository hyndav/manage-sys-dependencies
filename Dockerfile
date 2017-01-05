FROM java:8

RUN mkdir /app
COPY ./build/install/entitlement-data-migration /app
WORKDIR /app

RUN chmod +x /app/bin/entitlement-data-migration

ENTRYPOINT [ "/app/bin/entitlement-data-migration" ]
