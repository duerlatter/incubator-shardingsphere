/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

grammar DMLStatement;

import Symbol, Keyword, OracleKeyword, Literals, BaseRule, Comments;

insert
    : INSERT (insertSingleTable | insertMultiTable)
    ;

insertSingleTable
    : insertIntoClause (insertValuesClause | select)
    ;

insertMultiTable
    : (ALL multiTableElement+ | conditionalInsertClause) select
    ;

multiTableElement
    : insertIntoClause insertValuesClause
    ;

conditionalInsertClause
    : (ALL | FIRST)? conditionalInsertWhenPart+ conditionalInsertElsePart?
    ;

conditionalInsertWhenPart
    : WHEN expr THEN multiTableElement+
    ;

conditionalInsertElsePart
    : ELSE multiTableElement+
    ;

insertIntoClause
    : INTO tableName (AS? alias)?
    ;

insertValuesClause
    : columnNames? VALUES assignmentValues (COMMA_ assignmentValues)*
    ;

update
    : UPDATE updateSpecification? tableReferences setAssignmentsClause whereClause?
    ;

updateSpecification
    : ONLY
    ;

assignment
    : columnName EQ_ assignmentValue
    ;

setAssignmentsClause
    : SET assignment (COMMA_ assignment)*
    ;

assignmentValues
    : LP_ assignmentValue (COMMA_ assignmentValue)* RP_
    | LP_ RP_
    ;

assignmentValue
    : expr | DEFAULT
    ;

delete
    : DELETE deleteSpecification? (singleTableClause | multipleTablesClause) whereClause?
    ;

deleteSpecification
    : ONLY
    ;

singleTableClause
    : FROM? LP_? tableName RP_? (AS? alias)?
    ;

multipleTablesClause
    : multipleTableNames FROM tableReferences | FROM multipleTableNames USING tableReferences
    ;

multipleTableNames
    : tableName DOT_ASTERISK_? (COMMA_ tableName DOT_ASTERISK_?)*
    ;

select 
    : selectSubquery forUpdateClause?
    ;

selectSubquery
    : (queryBlock | selectUnionClause | parenthesisSelectSubquery) orderByClause? rowLimitingClause
    ;

selectUnionClause
    : ((queryBlock | parenthesisSelectSubquery) orderByClause? rowLimitingClause) ((UNION ALL? | INTERSECT | MINUS) selectSubquery)+
    ;

parenthesisSelectSubquery
    : LP_ selectSubquery RP_
    ;

unionClause
    : queryBlock (UNION (ALL | DISTINCT)? queryBlock)*
    ;

queryBlock
    : SELECT duplicateSpecification? projections fromClause? whereClause? groupByClause? havingClause?
    ;

duplicateSpecification
    : ALL | DISTINCT
    ;

projections
    : (unqualifiedShorthand | projection) (COMMA_ projection)*
    ;

projection
    : (columnName | expr) (AS? alias)? | qualifiedShorthand
    ;

alias
    : identifier | STRING_
    ;

unqualifiedShorthand
    : ASTERISK_
    ;

qualifiedShorthand
    : identifier DOT_ASTERISK_
    ;

fromClause
    : FROM tableReferences
    ;

tableReferences
    : tableReference (COMMA_ tableReference)*
    ;

tableReference
    : tableFactor joinedTable*
    ;

tableFactor
    : tableName (AS? alias)? | subquery AS? alias? columnNames? | LP_ tableReferences RP_
    ;

joinedTable
    : NATURAL? ((INNER | CROSS)? JOIN) tableFactor joinSpecification?
    | NATURAL? (LEFT | RIGHT | FULL) OUTER? JOIN tableFactor joinSpecification?
    ;

joinSpecification
    : ON expr | USING columnNames
    ;

whereClause
    : WHERE expr
    ;

groupByClause
    : GROUP BY orderByItem (COMMA_ orderByItem)*
    ;

havingClause
    : HAVING expr
    ;

subquery
    : LP_ selectSubquery RP_
    ;

forUpdateClause
    : FOR UPDATE (OF forUpdateClauseList)? ((NOWAIT | WAIT INTEGER_) | SKIP_SYMBOL LOCKED)?
    ;

forUpdateClauseList
    : forUpdateClauseOption (COMMA_ forUpdateClauseOption)*
    ;

forUpdateClauseOption
    : ((tableName | viewName) DOT_)? columnName
    ;

rowLimitingClause
    : (OFFSET offset (ROW | ROWS))? (FETCH (FIRST | NEXT) (rowcount | percent PERCENT)? (ROW | ROWS) (ONLY | WITH TIES))?
    ;

merge
    : MERGE hint? intoClause usingClause mergeUpdateClause? mergeInsertClause? errorLoggingClause?
    ;

hint
    : BLOCK_COMMENT | INLINE_COMMENT
    ;

intoClause
    : INTO (tableName | viewName) alias?
    ;

usingClause
    : USING ((tableName | viewName) | subquery) alias? ON LP_ expr RP_
    ;

mergeUpdateClause
    : WHEN MATCHED THEN UPDATE SET mergeSetAssignmentsClause whereClause? deleteWhereClause?
    ;

mergeSetAssignmentsClause
    : mergeAssignment (COMMA_ mergeAssignment)*
    ;

mergeAssignment
    : columnName EQ_ mergeAssignmentValue
    ;

mergeAssignmentValue
    : expr | DEFAULT
    ;

deleteWhereClause
    : DELETE whereClause
    ;

mergeInsertClause
    : WHEN NOT MATCHED THEN INSERT mergeInsertColumn? mergeColumnValue whereClause?
    ;

mergeInsertColumn
    : LP_ columnName (COMMA_ columnName)* RP_
    ;

mergeColumnValue
    : VALUES LP_ (expr | DEFAULT) (COMMA_ (expr | DEFAULT))* RP_
    ;

errorLoggingClause
    : LOG ERRORS (INTO tableName)? (LP_ simpleExpr RP_)? (REJECT LIMIT (numberLiterals | UNLIMITED))?
    ;
