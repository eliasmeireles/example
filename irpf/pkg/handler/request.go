package handler

import (
	"encoding/json"
	"irpf/pkg/model"
	"irpf/pkg/resource"
	"irpf/pkg/storage"
	"irpf/pkg/utils"
	"net/http"
	"strings"
)

func HomeHandler(w http.ResponseWriter, r *http.Request) {
	http.ServeFile(w, r, utils.GetEnvOrDefault("FRONTEND_FILE", "static/index.html"))
}

func ReloadProducts(w http.ResponseWriter, r *http.Request) {
	resource.ReloadProducts()
	w.WriteHeader(http.StatusOK)
}

func GetProdutos(w http.ResponseWriter, r *http.Request) {
	query := r.URL.Query()
	filtroNome := strings.ToLower(query.Get("nome"))
	tiposEvento := query["tipo_evento"]

	resultados := []model.Produto{}
	total := 0.0

	for _, p := range storage.Products {
		nomeMatch := filtroNome == "" || strings.Contains(strings.ToLower(p.Nome), filtroNome)
		tipoMatch := len(tiposEvento) == 0 || contains(tiposEvento, p.TipoEvento)

		if nomeMatch && tipoMatch {
			resultados = append(resultados, p)
			total += p.ValorLiquido
		}
	}

	response := map[string]interface{}{
		"produtos": resultados,
		"total":    total,
		"count":    len(resultados),
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}

func RmProdutos(w http.ResponseWriter, r *http.Request) {
	query := r.URL.Query()
	productName := strings.ToLower(query.Get("produto"))

	var resultados []model.Produto
	var removed []model.Produto

	for _, p := range storage.Products {
		if !strings.Contains(strings.ToLower(p.Nome), strings.ToLower(productName)) {
			resultados = append(resultados, p)
		} else {
			removed = append(removed, p)
		}
	}

	storage.Products = resultados

	response := map[string]interface{}{
		"removed": removed,
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}

func contains(slice []string, item string) bool {
	for _, s := range slice {
		if strings.EqualFold(s, item) {
			return true
		}
	}
	return false
}
