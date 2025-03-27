package model

type User struct {
	Username  string   `json:"username"`
	Password  string   `json:"password"`
	AuthToken string   `json:"authToken"`
	Roles     []string `json:"roles"`
}

func (u *User) GetId() string {
	return u.AuthToken
}

func (u *User) GetRoles() []string {
	return u.Roles
}

func (u *User) EncryptedPassword() string {
	return u.Password
}
