
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

logs:
	@docker logs -f file-server-go