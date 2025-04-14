%{
    #include<stdio.h>
    int count=0;
%}

DATA_TYPE "int"|"float"|"long"
KEYWORDS "main"|"if"|"else"
DIGIT [0-9]
LETTER [A-Za-z]
ID {LETTER}({LETTER}|{DIGIT})*
CONST {DIGIT}+
ARITH [\+\-\*/]
ASSIGN =

%%
{DATA_TYPE} {printf("Data Type: %s\n",yytext);}
{CONST} {printf("Constant: %s\n", yytext);}
{ID} {printf("Identifier: %s\n",yytext);count++;}
%%

int yywrap()
{
    return 1;
}

int main()
{
    yylex();
    printf("Number of Identifiers:%d\n",count);
    return 0;
}