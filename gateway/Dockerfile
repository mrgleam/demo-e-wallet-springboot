FROM kong/kong-gateway:3.9

USER root

COPY kong.yml /etc/kong/kong.yml
ENV KONG_DECLARATIVE_CONFIG=/etc/kong/kong.yml

RUN mkdir /usr/local/share/lua/5.1/kong/plugins/custom-jwt-claims-headers
COPY custom-jwt-claims-headers /usr/local/share/lua/5.1/kong/plugins/custom-jwt-claims-headers