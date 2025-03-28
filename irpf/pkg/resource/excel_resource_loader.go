package resource

import (
	"fmt"
	"github.com/360EntSecGroup-Skylar/excelize"
	"irpf/pkg/model"
	"irpf/pkg/storage"
	"irpf/pkg/utils"
	"log"
	"strings"
)

func ReloadProducts() {
	filePath := utils.GetEnvOrDefault("EXCEL_FILE", "")
	if filePath == "" {
		log.Fatalf("Env EXCEL_FILE was not set.")
	}

	err := LoadExcelData(filePath)
	if err != nil {
		log.Fatalf("Erro ao carregar dados do Excel: %v", err)
	}
}

func LoadExcelData(filename string) error {
	var localProducts []model.Produto

	f, err := excelize.OpenFile(filename)
	if err != nil {
		return err
	}

	// Assume que os dados estão na primeira planilha
	rows := f.GetRows(f.GetSheetName(1))

	rmLink := "/api/produtos/remover?produto="
	
	// Pula o cabeçalho e processa as linhas
	for _, row := range rows[1:] {
		if len(row) < 7 {
			continue
		}

		valorLiquido := 0.0
		// Converte o valor líquido de "R$ 1,23" para float64
		if len(row) >= 7 && row[6] != "" {
			valorStr := strings.Replace(strings.Replace(row[6], "R$", "", -1), ",", ".", -1)
			valorStr = strings.TrimSpace(valorStr)
			fmt.Sscanf(valorStr, "%f", &valorLiquido)
		}

		ticket := strings.TrimSpace(row[0])
		if idx := strings.Index(ticket, "-"); idx != -1 {
			ticket = strings.TrimSpace(ticket[:idx])
		}

		produto := model.Produto{
			Ticket:        ticket,
			Nome:          strings.TrimSpace(row[0]),
			TipoEvento:    strings.TrimSpace(row[2]),
			Instituicao:   strings.TrimSpace(row[3]),
			Quantidade:    strings.TrimSpace(row[4]),
			PrecoUnitario: strings.TrimSpace(row[5]),
			ValorLiquido:  valorLiquido,
			RmLink:        rmLink + ticket,
		}

		localProducts = append(localProducts, produto)
	}

	storage.Products = localProducts
	return nil
}
