import argparse
import json

SDK_KEY = "sdk"
VERSION_KEY = "version"
CHANGELOG_KEY = "changeLog"
TEST_COVERAGE_GAP_KEY = "testCoverageGap"


def create_json(sdk, version, changelog, coverage_gap, specificRecipient):
    json_to_send = {SDK_KEY: sdk, VERSION_KEY: version, CHANGELOG_KEY: changelog, TEST_COVERAGE_GAP_KEY: coverage_gap}
    if specificRecipient:
        json_to_send["specificRecipient"] = specificRecipient
    print(json.dumps(json_to_send))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Creates json for release")
    parser.add_argument('sdk', help="SDK Type")
    parser.add_argument('version', help="SDK Version")
    parser.add_argument('changeLog', help="Version Changelog")
    parser.add_argument('testCoverageGap', help="Test Coverage Gap")
    parser.add_argument('specificRecipient', help="Overide recipient group", nargs='?')
    args = parser.parse_args()
    create_json(args.sdk, args.version, args.changeLog, args.testCoverageGap, args.specificRecipient)
