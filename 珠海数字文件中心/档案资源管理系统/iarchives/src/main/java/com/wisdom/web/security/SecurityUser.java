package com.wisdom.web.security;

import com.wisdom.web.entity.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */
public class SecurityUser extends Tb_user implements UserDetails {

    private List<Tb_role> roles;//角色

    private List<Tb_group> groups;//组

    private List<Tb_right_function> functions;//功能

    private Tb_Personalized personalized;//个性化

    private List<String> resources;//资源

    private String type;//平台类型

    private String platformchange;//平台切换记录

    public String getPlatformchange() {
        return platformchange;
    }

    public void setPlatformchange(String platformchange) {
        this.platformchange = platformchange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public Tb_Personalized getPersonalized() {
        return personalized;
    }

    public void setPersonalized(Tb_Personalized personalized) {
        this.personalized = personalized;
    }

    public List<Tb_role> getRoles() {
        return roles;
    }

    public void setRoles(List<Tb_role> roles) {
        this.roles = roles;
    }


    public List<Tb_group> getGroups() {
        return groups;
    }

    public void setGroups(List<Tb_group> groups) {
        this.groups = groups;
    }

    public List<Tb_right_function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Tb_right_function> functions) {
        this.functions = functions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_root");//root角色特权
        grantedAuthorities.add(grantedAuthority);
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired(){
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled(){
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public String getPassword() {
        return super.getLoginpassword();
    }

    @Override
    public String getUsername() {
        return super.getLoginname();
    }

    @Override
    public String toString() {
        return this.getUsername();
    }

    @Override
    public int hashCode() {
        return this.getUsername().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }
}
