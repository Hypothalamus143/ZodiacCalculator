package com.example.zodiaccalculator.data.utils

import net.objecthunter.exp4j.ExpressionBuilder

class ExpressionEvaluator {

    // Regular evaluation - returns null if can't evaluate
    fun evaluate(expression: String, variables: Map<String, Double>): Double? {
        return try {
            val builder = ExpressionBuilder(expression)

            // Add each variable individually
            variables.keys.forEach { varName ->
                if (expression.contains(varName)) {
                    builder.variable(varName)
                }
            }

            val expr = builder.build()

            // Set variable values
            variables.forEach { (name, value) ->
                if (expression.contains(name)) {
                    expr.setVariable(name, value)
                }
            }

            expr.evaluate()
        } catch (e: Exception) {
            null
        }
    }

    // Symbolic evaluation - substitute known values, leave unknowns as symbols
    fun getSymbolicExpression(expression: String, variables: Map<String, Double>): String {
        var result = expression

        // Sort by name length (longest first) to avoid partial replacements
        val sortedVariables = variables.entries.sortedByDescending { it.key.length }

        sortedVariables.forEach { (name, value) ->
            // Replace variable names with their values using word boundaries
            val regex = Regex("\\b$name\\b")
            result = result.replace(regex, value.toString())
        }

        return result
    }

    // Check if expression is fully evaluable
    fun isFullyEvaluable(expression: String, variables: Map<String, Double>): Boolean {
        return try {
            evaluate(expression, variables) != null
        } catch (e: Exception) {
            false
        }
    }

    // Get list of undefined variables in expression
    fun getUndefinedVariables(expression: String, definedVariables: Set<String>): List<String> {
        val undefined = mutableSetOf<String>()
        // Regex to find variable names (letters and numbers, starting with letter)
        val regex = Regex("[a-zA-Z][a-zA-Z0-9]*")
        val matches = regex.findAll(expression)

        matches.forEach { match ->
            val varName = match.value
            if (!definedVariables.contains(varName) && !isNumeric(varName)) {
                undefined.add(varName)
            }
        }

        return undefined.toList()
    }

    private fun isNumeric(str: String): Boolean {
        return str.toDoubleOrNull() != null
    }
}