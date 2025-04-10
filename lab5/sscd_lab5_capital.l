%{
int count = 0;
%}
L [A-Z]
l [a-z]
N [.]
R [\n]
%%

{L} {printf("%s capital letter\n", yytext);count++;}
{l} {printf("%s lowercase letter\n", yytext);}
{N} {printf("%s not a capital letter\n", yytext);}
{R} {return 0;}
. ;
%%
int yywrap(){}
int main(){
yylex();
printf("\n Number of Capital Letters in the given input - %d\n", count);
return 0;
}
