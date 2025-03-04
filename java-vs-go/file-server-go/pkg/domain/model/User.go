package model

type User struct {
	Username  string   `json:"username"`
	Password  string   `json:"password"`
	AuthToken string   `json:"authToken"`
	Roles     []string `json:"roles"`
}
