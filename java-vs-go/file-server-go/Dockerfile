# Use the official Golang image
FROM golang:1.23-alpine as builder

# Set the working directory
WORKDIR /app


# Copy go mod and sum files
COPY go.mod go.sum ./

RUN mkdir "gen"

RUN go install github.com/oapi-codegen/oapi-codegen/v2/cmd/oapi-codegen@latest

COPY . .

RUN oapi-codegen --config resources/config.yaml resources/openapi.yaml

# Download dependencies
RUN go mod tidy

# Copy the entire source code

# Build the Go app
RUN go build -o file-server cmd/server/main.go

FROM alpine:latest

LABEL mainers=eliasmflilico@gmail.com

# Required by container health check
RUN apk add --no-cache curl

RUN mkdir /opt/resources

ARG PORT

COPY --from=builder  app/file-server /bin/file-server
COPY --from=builder  app/resources/users.json /opt/resources/users.json
COPY --from=builder  app/resources/openapi.yaml /opt/resources/openapi.yaml

ENV ENV_STORAGE_PATH /file-server
ENV API_SECRET_KEY fdotWXwes9ngUblnI9owMP2Z
ENV AUTHORIZATION_RESOURCE_PATH /opt/resources/users.json
ENV OPENAPI_RESOURCE_PATH /opt/resources/openapi.yaml
ENV JWT_ISSUER file-server-go
ENV CONTEXT_PATH /api/file-server/v1/
ENV PORT $PORT

ENV TZ America/Sao_Paulo

EXPOSE $PORT

CMD ["file-server"]
