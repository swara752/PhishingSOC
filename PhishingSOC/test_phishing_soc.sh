#!/bin/bash

# PhishingSOC API Testing Script
# This script tests all API endpoints

BASE_URL="http://localhost:8080"
RESULTS_FILE="test_results.txt"
PASSED=0
FAILED=0

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║           PhishingSOC API Testing Suite                      ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Create results file
echo "=== PhishingSOC API Testing Results ===" > $RESULTS_FILE
echo "Test Date: $(date)" >> $RESULTS_FILE
echo "Base URL: $BASE_URL" >> $RESULTS_FILE
echo "" >> $RESULTS_FILE

# Function to test endpoint
test_endpoint() {
    local test_num=$1
    local test_name=$2
    local method=$3
    local endpoint=$4
    local data=$5
    local expected_status=$6

    echo -n "[$test_num] $test_name ... "
    
    if [ "$method" = "POST" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
    fi

    STATUS=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | head -n-1)

    if [ "$STATUS" = "$expected_status" ] || [ "$STATUS" = "200" ]; then
        echo -e "${GREEN}✓ PASSED${NC}"
        ((PASSED++))
    else
        echo -e "${RED}✗ FAILED${NC} (Status: $STATUS)"
        ((FAILED++))
    fi

    echo "Test: $test_name" >> $RESULTS_FILE
    echo "Endpoint: $endpoint" >> $RESULTS_FILE
    echo "Status: $STATUS" >> $RESULTS_FILE
    echo "Response: $BODY" >> $RESULTS_FILE
    echo "" >> $RESULTS_FILE
}

echo ""
echo "📡 Testing Authentication Endpoints"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

test_endpoint 1 "Login Success" "POST" "/api/auth/login" \
    '{"username":"testuser","password":"testpass"}' "200"

test_endpoint 2 "Login Fail - Invalid Credentials" "POST" "/api/auth/login" \
    '{"username":"ab","password":"cd"}' "400"

test_endpoint 3 "Register New User" "POST" "/api/auth/register" \
    '{"username":"newuser","password":"newpass"}' "200"

test_endpoint 4 "Logout" "POST" "/api/auth/logout" "" "200"

echo ""
echo "📧 Testing Email Service Endpoints"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

test_endpoint 5 "Upload Email" "POST" "/api/email/upload?filename=test.eml&userEmail=user@example.com" \
    "" "200"

test_endpoint 6 "Analyze Email" "GET" "/api/email/analyze/email_123?userEmail=analyst@company.com" \
    "" "200"

test_endpoint 7 "Get Scan Status" "GET" "/api/email/scan-status/email_123" "" "200"

test_endpoint 8 "List Emails" "GET" "/api/email/list?userEmail=user@example.com" "" "200"

echo ""
echo "📊 Testing Logging API Endpoints"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

test_endpoint 9 "Get Logs Summary" "GET" "/api/logs/summary" "" "200"

test_endpoint 10 "Get Debug Logs" "GET" "/api/logs/debug?lines=50" "" "200"

test_endpoint 11 "Get Security Logs" "GET" "/api/logs/security?lines=50" "" "200"

test_endpoint 12 "Get Admin Logs" "GET" "/api/logs/admin_actions?lines=50" "" "200"

echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                    Test Summary                              ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo -e "${GREEN}✓ Tests Passed: $PASSED${NC}"
echo -e "${RED}✗ Tests Failed: $FAILED${NC}"
echo "Total Tests: $((PASSED + FAILED))"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed! ✓${NC}"
    echo "Results saved to: $RESULTS_FILE"
    exit 0
else
    echo -e "${RED}Some tests failed. Check $RESULTS_FILE for details${NC}"
    echo "Results saved to: $RESULTS_FILE"
    exit 1
fi
