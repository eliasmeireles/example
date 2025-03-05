counter = 50

restart:
	@docker restart file-server-java
	@docker restart file-server-go

up:
	@cd file-server-go && make local-deployment
	@cd file-server-java && make local-deployment

down:
	@cd file-server-go && make local-down
	@cd file-server-java && make local-down

build:
	@go build -o bin/$$(uname -m)/benchmark  benchmark.go

test:
	@./bin/$$(uname -m)/benchmark --count $(counter) --async

test-sync:
	@./bin/$$(uname -m)/benchmark --count $(counter) --sync

test-fresh:
	@make down
	@make up
	@echo "Waiting 15 seconds for containers get ready..."
	@sleep 15
	@docker ps | grep file-server
	@./bin/$$(uname -m)/benchmark --count $(counter) --async


test-fresh-sync:
	@make down
	@make up
	@echo "Waiting 15 seconds for containers get ready..."
	@sleep 15
	@docker ps | grep file-server
	@./bin/$$(uname -m)/benchmark --count $(counter)  --sync
