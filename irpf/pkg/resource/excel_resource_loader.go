package resource

import (
	"fmt"
	"github.com/360EntSecGroup-Skylar/excelize"
	"irpf/pkg/model"
	"irpf/pkg/storage"
	"strings"
)

func LoadExcelData(filename string) error {
	f, err := excelize.OpenFile(filename)
	if err != nil {
		return err
	}

	// Assume que os dados estão na primeira planilha
	rows := f.GetRows(f.GetSheetName(1))

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

		produto := model.Produto{
			Nome:          strings.TrimSpace(row[0]),
			TipoEvento:    strings.TrimSpace(row[2]),
			Instituicao:   strings.TrimSpace(row[3]),
			Quantidade:    strings.TrimSpace(row[4]),
			PrecoUnitario: strings.TrimSpace(row[5]),
			ValorLiquido:  valorLiquido,
		}

		storage.Products = append(storage.Products, produto)
	}

	return nil
}
