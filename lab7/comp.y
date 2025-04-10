    %{
#include<stdio.h>
extern int yylex();
extern int yywrap();
extern int yyparse();
extern char* yytext;
%}

%token IF WHILE FOR OP CP OCB CCB CMP SC ASG ID NUM COMMA OPR

%%
start:  sif | swhile | sfor;

sif:    IF OP cmpn CP stmt    {
    printf("Parsed IF statement\n");
    printf("VALID STATEMENT IF\n");
};

swhile: WHILE OP cmpn CP stmt
 {
    printf("Parsed WHILE statement\n");
    printf("VALID STATEMENT WHILE\n");
};

sfor:   FOR OP init cmpn SC inc CP stmt {
    printf("Parsed FOR statement\n");
    printf("VALID STATEMENT FOR\n");
};

cmpn:   ID CMP ID     { printf("Parsed comparison between two identifiers\n"); }
      | ID CMP NUM    { printf("Parsed comparison between identifier and number\n"); };

stmt:   ID ASG NUM SC { printf("Parsed assignment statement\n"); };

init:   ID ASG NUM SC { printf("Parsed initialization\n"); };

inc:    ID ASG ID OPR NUM    { printf("Parsed increment with arithmetic\n"); }
      | ID ASG NUM           { printf("Parsed simple increment\n"); };

%%
int yyerror(char *str)
{
    if (yytext[0] == 'w' || yytext[1] == 'h')
        printf("Invalid WHILE statement!\n");
    else if (yytext[0] == 'f' && yytext[1] == 'o')
        printf("Invalid FOR statement!\n");
    else
        printf("Invalid IF statement!\n");
    printf("%s\n", str);
    return 0;
}

int main()
{
    yyparse();
    return 1;
}
