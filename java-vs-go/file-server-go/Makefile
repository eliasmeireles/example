
local-deployment:
	@docker-compose -p filer-server-go up --build -d

local-down:
	@docker-compose -p filer-server-go down

config:
	@go mod tidy
	@oapi-codegen --config resources/config.yaml resources/openapi.yaml

run:
	@make config
	@AUTHORIZATION_RESOURCE_PATH=./resources/users.json API_SECRET_AUTHORIZATION=fdotWXwes9ngUblnI9owMP2Z  go run ./cmd/server/main.go
