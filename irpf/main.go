package main

import (
	"fmt"
	"github.com/gorilla/mux"
	"irpf/pkg/handler"
	"irpf/pkg/resource"
	"irpf/pkg/utils"
	"log"
	"net/http"
)

func main() {

	filePath := utils.GetEnvOrDefault("EXCEL_FILE", "")
	if filePath == "" {
		log.Fatalf("Env EXCEL_FILE was not set.")
	}

	err := resource.LoadExcelData(filePath)
	if err != nil {
		log.Fatalf("Erro ao carregar dados do Excel: %v", err)
	}

	r := mux.NewRouter()
	r.HandleFunc("/api/produtos", handler.GetProdutos).Methods("GET")
	r.HandleFunc("/", handler.HomeHandler).Methods("GET")
	r.PathPrefix("/static/").
		Handler(http.StripPrefix("/static/", http.FileServer(http.Dir("static"))))

	appPort := utils.GetEnvOrDefault("APP_PORT", "1032")
	fmt.Printf("Application is running on port http://localhost:%s", appPort)
	log.Fatal(http.ListenAndServe(":"+appPort, r))
}
