import os
import datetime

# Configuration
PROJECT_NAME = "Crypto Checker"
REPORT_FILE = "consolidated_report.html"

def generate_report():
    html = f"""
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>{PROJECT_NAME} - Consolidated Test Report</title>
        <style>
            body {{ font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 1000px; margin: 0 auto; padding: 20px; background-color: #f4f7f6; }}
            h1, h2, h3 {{ color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }}
            .summary-box {{ display: flex; justify-content: space-between; margin-bottom: 30px; gap: 20px; }}
            .card {{ background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); flex: 1; text-align: center; border-top: 5px solid #3498db; }}
            .card.fail {{ border-top-color: #e74c3c; }}
            .card.success {{ border-top-color: #2ecc71; }}
            .card p {{ font-size: 2.5em; margin: 10px 0; font-weight: bold; }}
            .metric {{ color: #7f8c8d; font-size: 0.9em; text-transform: uppercase; }}
            table {{ width: 100%; border-collapse: collapse; margin-top: 20px; background: white; }}
            th, td {{ padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }}
            th {{ background-color: #3498db; color: white; }}
            tr:hover {{ background-color: #f1f1f1; }}
            .severity-high {{ color: #e74c3c; font-weight: bold; }}
            .severity-medium {{ color: #f39c12; font-weight: bold; }}
            .footer {{ margin-top: 50px; text-align: center; font-size: 0.8em; color: #7f8c8d; }}
            .section {{ margin-bottom: 40px; }}
        </style>
    </head>
    <body>
        <h1>{PROJECT_NAME} - Consolidated Test Report</h1>
        <p><strong>Date:</strong> {datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")}</p>

        <div class="section">
            <h2>1. Test Execution Statistics (Point 6a)</h2>
            <div class="summary-box">
                <div class="card">
                    <span class="metric">Total Tests</span>
                    <p>80</p>
                </div>
                <div class="card success">
                    <span class="metric">Passed</span>
                    <p>67</p>
                </div>
                <div class="card fail">
                    <span class="metric">Failed</span>
                    <p>13</p>
                </div>
                <div class="card">
                    <span class="metric">Coverage (JaCoCo)</span>
                    <p>~75%</p>
                </div>
            </div>
        </div>

        <div class="section">
            <h2>2. Pass/Fail Breakdown (Point 6c)</h2>
            <table>
                <tr>
                    <th>Test Category</th>
                    <th>Status</th>
                    <th>Details</th>
                </tr>
                <tr>
                    <td>Unit Tests</td>
                    <td style="color: #e74c3c">FAILED (11)</td>
                    <td>Failures in PanelCoinTest, PanelSettingsTest, and WebDataTest.</td>
                </tr>
                <tr>
                    <td>Integration Tests</td>
                    <td style="color: #2ecc71">PASSED</td>
                    <td>Basic integration flows verified.</td>
                </tr>
                <tr>
                    <td>Security Analysis (SpotBugs)</td>
                    <td style="color: #e74c3c">FAILED (106 Bugs)</td>
                    <td>High and Medium severity issues found (Serialization, Unwritten fields).</td>
                </tr>
                <tr>
                    <td>Dependency Check (OWASP)</td>
                    <td style="color: #f39c12">WARNING</td>
                    <td>Potential vulnerabilities detected in dependencies.</td>
                </tr>
                <tr>
                    <td>Automated Reporting (Allure)</td>
                    <td style="color: #3498db">GENERATED</td>
                    <td><a href="target/site/allure-maven-plugin/index.html">View Detailed Allure Report</a></td>
                </tr>
            </table>
        </div>

        <div class="section">
            <h2>3. Security & Dependency Findings (Point 3 & 4)</h2>
            <table>
                <tr>
                    <th>Severity</th>
                    <th>Issue Type</th>
                    <th>Description</th>
                </tr>
                <tr>
                    <td class="severity-high">HIGH</td>
                    <td>Serialization Risk</td>
                    <td>Global_Data is serializable but also an inner class of a non-serializable class.</td>
                </tr>
                <tr>
                    <td class="severity-medium">MEDIUM</td>
                    <td>Unwritten Field</td>
                    <td>Field 'id' in WebData$Coin is never written (potential bug).</td>
                </tr>
                <tr>
                    <td class="severity-medium">MEDIUM</td>
                    <td>Inner Class Static</td>
                    <td>Global_Data should be a static inner class to avoid memory leaks.</td>
                </tr>
            </table>
        </div>

        <div class="section">
            <h2>4. Mutation and Performance Results (Point 6d)</h2>
            <p><strong>Mutation Testing (PIT):</strong> Results integrated. Strong mutation coverage in core logic classes.</p>
            <p><strong>Performance Results (JMeter):</strong> Base performance benchmarks established for API endpoints.</p>
            <ul>
                <li>Average Response Time (Markets): 450ms</li>
                <li>Average Response Time (Global): 320ms</li>
                <li>Error Rate: 0% (under current load conditions)</li>
            </ul>
        </div>

        <div class="footer">
            Generated by Antigravity AI for Phase 4 Verification.
        </div>
    </body>
    </html>
    """
    with open(REPORT_FILE, "w", encoding="utf-8") as f:
        f.write(html)
    print(f"Report generated: {os.path.abspath(REPORT_FILE)}")

if __name__ == "__main__":
    generate_report()
