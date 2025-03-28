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
	resource.ReloadProducts()

	r := mux.NewRouter()
	r.HandleFunc("/api/reload", handler.ReloadProducts).Methods("GET")
	r.HandleFunc("/api/produtos", handler.GetProdutos).Methods("GET")
	r.HandleFunc("/api/produtos/remover", handler.RmProdutos).Methods("GET")
	r.HandleFunc("/", handler.HomeHandler).Methods("GET")
	r.PathPrefix("/static/").
		Handler(http.StripPrefix("/static/", http.FileServer(http.Dir("static"))))

	appPort := utils.AppPort()
	fmt.Printf("Application is running on port http://localhost:%s", appPort)
	log.Fatal(http.ListenAndServe(":"+appPort, r))
}
