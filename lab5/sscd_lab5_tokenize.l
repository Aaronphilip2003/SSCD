%{
#include <stdio.h>
int count = 0;

struct symtab {
	int id;
	char name[50];
	char class[50];
};
%}

DATA_type "int"|"float"|"long"
keywords "main"|"if"|"else"|"for"|"while"|"return"
DIGIT   [0-9]
LETTER  [a-zA-Z]
ID      {LETTER}({LETTER}|{DIGIT})*
func_name {ID}\(\)
CONST   {DIGIT}+
ARITH_OP  [\+\-\*/]
ASSIGN    =

%%
{DATA_type}  { printf("Data type: %s\n", yytext); }
{func_name}  { printf("Function call: %s\n", yytext); }
{CONST}      { printf("Constant: %s\n", yytext); }
{ARITH_OP}   { printf("Arithmetic Symbol: %s\n", yytext); }
{ASSIGN}     { printf("Assignment operator: %s\n", yytext); }
{ID}         { printf("Identifier: %s\n", yytext); count++; }
[ \t\n]      ;
.            { printf("Unknown: %s\n", yytext); }
%%

int yywrap() {
	return 1;
}

int main() {
	yylex();
	printf("\nNumber of Identifiers in the given input - %d\n", count);
	return 0;
}

