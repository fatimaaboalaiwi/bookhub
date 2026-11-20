
import json
import time
import urllib.request
import urllib.error
from getpass import getpass
from concurrent.futures import ThreadPoolExecutor
from threading import Barrier

BASE_URL = "http://localhost:8001/api/v1"
BOOK_ID = 6

PURCHASE_URL = f"{BASE_URL}/books/{BOOK_ID}/purchase"
BOOK_URL = f"{BASE_URL}/books/{BOOK_ID}"


def check_token(name, token):
    request = urllib.request.Request(
        BOOK_URL,
        headers={"Authorization": f"Bearer {token}"},
        method="GET"
    )

    try:
        with urllib.request.urlopen(request, timeout=15) as response:
            if response.status == 200:
                print(f"{name}: Authentication successful.")
                return True

    except urllib.error.HTTPError as error:
        print(f"{name}: Authentication failed (HTTP {error.code}).")
    except Exception as error:
        print(f"{name}: Could not connect: {error}")

    return False


def send_purchase(name, token, barrier):
    request = urllib.request.Request(
        PURCHASE_URL,
        data=b"",
        headers={
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        },
        method="POST"
    )

    barrier.wait()

    start = time.perf_counter()

    try:
        with urllib.request.urlopen(request, timeout=30) as response:
            body = response.read().decode("utf-8")
            status = response.status

    except urllib.error.HTTPError as error:
        body = error.read().decode("utf-8")
        status = error.code

    except Exception as error:
        return {
            "user": name,
            "error": str(error)
        }

    elapsed = time.perf_counter() - start

    return {
        "user": name,
        "status": status,
        "elapsed_seconds": round(elapsed, 4),
        "body": body
    }


print("=" * 50)
print("BOOKHUB SIMULTANEOUS PURCHASE TEST")
print("=" * 50)

print("\nSTEP 1: Enter the ADMIN JWT.")
print("Paste the token without the word Bearer.")
admin_token = getpass("Admin JWT: ").strip()

if not admin_token:
    raise SystemExit("\nERROR: Admin JWT is empty. Test cancelled.")

print("\nSTEP 2: Enter the REGULAR USER JWT.")
print("Paste the token without the word Bearer.")
user_token = getpass("Regular user JWT: ").strip()

if not user_token:
    raise SystemExit("\nERROR: Regular user JWT is empty. Test cancelled.")

if admin_token == user_token:
    raise SystemExit("\nERROR: Both tokens are identical. Test cancelled.")

print("\nBoth tokens received.")
print("\nSTEP 3: Checking authentication...")

if not check_token("ADMIN", admin_token):
    raise SystemExit("Admin authentication failed. No purchases were sent.")

if not check_token("REGULAR USER", user_token):
    raise SystemExit("Regular user authentication failed. No purchases were sent.")

print("\nBoth users authenticated successfully.")

print("\nSTEP 4: Preparing simultaneous purchase requests...")

barrier = Barrier(3)

with ThreadPoolExecutor(max_workers=2) as executor:

    admin_future = executor.submit(
        send_purchase, "ADMIN", admin_token, barrier
    )

    user_future = executor.submit(
        send_purchase, "REGULAR USER", user_token, barrier
    )

    print("\nBoth requests are prepared and waiting.")

    for seconds in range(10, 0, -1):
        print(f"Starting simultaneously in {seconds}...")
        time.sleep(1)

    print("\nRELEASING BOTH REQUESTS NOW!\n")

    barrier.wait()

    results = [
        admin_future.result(),
        user_future.result()
    ]

print("=" * 50)
print("PURCHASE RESULTS")
print("=" * 50)

for result in results:
    print(json.dumps(result, indent=2))

print("\nSTEP 5: Checking final book quantity...")

get_request = urllib.request.Request(
    BOOK_URL,
    headers={"Authorization": f"Bearer {admin_token}"},
    method="GET"
)

try:
    with urllib.request.urlopen(get_request, timeout=15) as response:
        book = json.loads(response.read().decode("utf-8"))

        print(json.dumps({
            "bookId": book.get("id"),
            "title": book.get("title"),
            "qty": book.get("qty")
        }, indent=2))

except Exception as error:
    print("Could not retrieve final book quantity:", error)

print("\nTEST FINISHED.")
