
local-deployment:
	@docker-compose -p filer-server-go up --build -d

local-down:
	@docker-compose -p filer-server-go down

config:
	@go mod tidy
	@oapi-codegen --config resources/config.yaml resources/openapi.yaml

run:
	@make config
	@JWT_ISSUER=file-server-go AUTHORIZATION_RESOURCE_PATH=./resources/users.json API_SECRET_AUTHORIZATION=fdotWXwes9ngUblnI9owMP2Z  go run ./cmd/server/main.go

codegen:
	 @mkdir -p ./gen/ || true
	 @rm -rf ./gen/** || true
	 @touch ./gen/api.gen.go
	 @oapi-codegen --config ./resources/config.yaml ./resources/openapi.yaml

# Setup Buildx builder
buildx:
	@docker buildx create --name buildxBuilder --use
	@docker buildx inspect buildxBuilder --bootstrap

build:
	docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/eliasmeireles/go-file-server:v1.0.0 --push .

logs:
	@docker logs -f file-server-go