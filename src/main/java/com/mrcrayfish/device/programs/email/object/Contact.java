package com.mrcrayfish.device.programs.email.object;

/**
 * Author: MrCrayfish
 */
public class Contact
{
    private final String nickname;
    private final String email;

    public Contact(String nickname, String email)
    {
        this.nickname = nickname;
        this.email = email;
    }

    public String getNickname()
    {
        return nickname;
    }

    public String getEmail()
    {
        return email;
    }

    @Override
    public String toString()
    {
        return nickname;
    }
}
