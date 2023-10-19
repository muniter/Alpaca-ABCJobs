from common.shared.database.seed import seed

print("Recreating database")
seed()
print("Database recreated")
exit(0)
