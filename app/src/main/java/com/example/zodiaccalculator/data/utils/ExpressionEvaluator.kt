package com.example.zodiaccalculator.data.utils

import net.objecthunter.exp4j.ExpressionBuilder

class ExpressionEvaluator {

    fun evaluate(expression: String, variables: Map<String, Double>): Double? {
        return try {
            // Build the expression with all variable names
            val builder = ExpressionBuilder(expression)

            // Add all variable names that appear in the expression
            variables.keys.forEach { varName ->
                if (expression.contains(varName)) {
                    builder.variable(varName)
                }
            }

            // Build the expression
            val expr = builder.build()

            // Set the variable values
            variables.forEach { (name, value) ->
                if (expression.contains(name)) {
                    expr.setVariable(name, value)
                }
            }

            // Evaluate and return result
            expr.evaluate()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // For display - shows expression with current values substituted
    fun getExpressionWithValues(expression: String, variables: Map<String, Double>): String {
        var result = expression
        variables.forEach { (name, value) ->
            result = result.replace(name, value.toString())
        }
        return result
    }

    // Validate if an expression is syntactically correct
    fun isValidExpression(expression: String, availableVariables: List<String>): Boolean {
        return try {
            val builder = ExpressionBuilder(expression)
            availableVariables.forEach { varName ->
                if (expression.contains(varName)) {
                    builder.variable(varName)
                }
            }
            builder.build()
            true
        } catch (e: Exception) {
            false
        }
    }
}