package model

type Produto struct {
	Ticket        string  `json:"ticket"`
	Nome          string  `json:"nome"`
	TipoEvento    string  `json:"tipo_evento"`
	Instituicao   string  `json:"instituicao"`
	Quantidade    string  `json:"quantidade"`
	PrecoUnitario string  `json:"preco_unitario"`
	ValorLiquido  float64 `json:"valor_liquido"`
	RmLink        string  `json:"rmLink"`
}
