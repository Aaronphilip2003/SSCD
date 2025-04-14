%{
    #include<stdio.h>
    #include<stdlib.h>
    void yyerror(char *s)
%}

%union{
    char fchar;
    double fval;
    int intval;
};

