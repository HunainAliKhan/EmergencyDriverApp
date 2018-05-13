package com.example.hunain.emergencydriverapp.Business_Object;

import com.example.hunain.emergencydriverapp.Entity.Token;

/**
 * Created by hunain on 4/7/2018.
 */

public interface ITokenBAO {
    void InsertToken(Token token);
    void UpdateToken(String token);
}
