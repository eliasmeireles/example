package repository

import (
	"encoding/json"
	"fmt"
	"log"
	"os"
	"sync"

	"file-server-go/pkg/domain/model"
	"file-server-go/pkg/env"
)

type UserRepository interface {
	// GetByUsername fetches a user by their username from the repository.
	GetByUsername(username string) (model.User, error)

	// GetByAuthToken fetches a user by their authentication token from the repository.
	GetByAuthToken(authToken string) (model.User, error)
}

type _userRepositoryImpl struct {
	users []model.User
}

func (repo *_userRepositoryImpl) GetByUsername(username string) (model.User, error) {

	for _, user := range repo.users {
		if user.Username == username {
			return user, nil
		}
	}
	return model.User{}, fmt.Errorf("user with username '%s' not found", username)
}

func (repo *_userRepositoryImpl) GetByAuthToken(authToken string) (model.User, error) {

	for _, user := range repo.users {
		if user.AuthToken == authToken {
			return user, nil
		}
	}
	return model.User{}, fmt.Errorf("user with authToken '%s' not found", authToken)
}

var (
	instance *_userRepositoryImpl
	once     sync.Once
)

// NewUserRepository initializes a UserRepositoryJSON instance by loading users from a JSON file.
func NewUserRepository() UserRepository {
	var err error
	once.Do(func() {
		instance = &_userRepositoryImpl{}
		err = instance.load(env.GetAppEnv().AuthorizationResourcePath)
	})
	if err != nil {
		panic(err)
	}
	return instance
}

// load reads and parses a JSON file containing user information.
func (repo *_userRepositoryImpl) load(filePath string) error {
	file, err := os.Open(filePath)
	if err != nil {
		return err
	}

	defer func(file *os.File) {
		err := file.Close()
		if err != nil {
			log.Fatal(err)
		}
	}(file)

	decoder := json.NewDecoder(file)
	return decoder.Decode(&repo.users)
}
