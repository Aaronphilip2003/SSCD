#include<stdio.h>
#include<string.h>
#include<ctype.h>

char input[10];
int i,error;

void E();
void T();
void EPrime();
void TPrime();
void F();

void main()
{
    i=0;
    error=0;
    printf("Enter the arithmetic expression:");
    ets(input);
    E();
    if(strlen(input)==i && error==0)
    {
        printf("Accepted\n");
    }
    else
    {
        printf("Rejected :/ \n");
    }
}

void E()
{
    T();
    EPrime();
}

void EPrime()
{
    if(input[i]=='+'){
        i++;
        T();
        EPrime();
    }
}

void T()
{
    F();
    TPrime();
}

void TPrime() {
  if (input[i] == '*') {
    i++;
    F();
    TPrime();
  }
}

void F()
{
    if(isalnum(input[i]))
    {
        i++;
    }
    else if (input[i]=='(') {
        i++;
        E();
        if(input[i]==')')
        i++;
    else
    error=1;
    }
    else
    error=1;
}