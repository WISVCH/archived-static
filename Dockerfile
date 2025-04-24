# syntax=docker.io/docker/dockerfile:1.7-labs

FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html
RUN rm /etc/nginx/conf.d/default.conf

COPY . /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/wisvch.conf