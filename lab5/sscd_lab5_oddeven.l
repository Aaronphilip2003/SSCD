%{
int c = 0;
%}
%%
[0-9]+ {c = atoi(yytext);
	if(c%2 == 0)
		printf("%s is Even", yytext);
	else
		printf("%s is Odd", yytext);
}
%%
int yywrap(){}
int main(){
yylex();
return 0;
}
