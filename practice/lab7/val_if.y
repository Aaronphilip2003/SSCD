%{
    #include<stdio.h>
%}

%token IF LPAREN RPAREN CMP OPR ASG ID NUM SC

%%
start: sif;
sif: IF LPAREN CMPN RPAREN stmt {printf("Validating if statement\n");}
stmt: ID ASG NUM SC {printf("Parsed Assignment Statement\n");}
CMPN: ID CMP ID | ID CMP NUM {printf("Parsed Comparison Statement");}
%%

int yyerror(char *s)
{
    printf("Error Occured\n");
}

int main()
{
    yyparse();
    return 1;
}