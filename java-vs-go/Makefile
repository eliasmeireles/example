counter = 50
up:
	@cd file-server-go && make local-deployment
	@cd file-server-java && make local-deployment

down:
	@cd file-server-go && make local-down
	@cd file-server-java && make local-down

build:
	@go build -o bin/$$(uname -m)/benchmark  benchmark.go

test:
	@./bin/$$(uname -m)/benchmark --count $(counter)

test-fresh:
	@make down
	@make up
	@echo "Waiting 15 seconds for containers get ready..."
	@sleep 15
	@docker ps | grep file-server
	@./bin/$$(uname -m)/benchmark --count $(counter)
