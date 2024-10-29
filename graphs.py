import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

def graph_payment_distribution():
    np.random.seed(42)
    num_clients = 10000
    sla_values = 0.1 + np.random.rand(num_clients) * 0.9
    payments = 1.0 / sla_values

    plt.figure(figsize=(8, 6))
    plt.hist(payments, bins=10, color='skyblue', edgecolor='black')
    plt.title("Payment Distribution", fontsize=14)
    plt.xlabel("Payment", fontsize=12)
    plt.ylabel("Number of Clients", fontsize=12)
    plt.grid(True)
    plt.legend()

    plt.savefig("images\payments_distribution.png")
    plt.show()

def graph_reachability_prob_vs_distance():
    distanze = np.linspace(0, 1100, 1100)
    probabilities = [calculate_reachability_probability(d) for d in distanze]

    plt.figure(figsize=(8, 6))
    plt.plot(distanze, probabilities, label="Reachability Probability", color='blue', linewidth=2)
    plt.title("Reachability Probability vs Distance", fontsize=14)
    plt.xlabel("Distance", fontsize=12)
    plt.ylabel("Reachability Probability", fontsize=12)
    plt.grid(True)
    plt.legend()

    plt.savefig("images\\reachability_probability.png")
    plt.show()
    
def calculate_reachability_probability(distance):
    max_probability = 1
    min_probability = 0.1
    min_distance = 0
    max_distance = 1000
    if distance < min_distance:
        return max_probability
    elif distance > max_distance:
        return min_probability
    else:
        return max_probability - (max_probability - min_probability) * (distance - min_distance) / (max_distance - min_distance)

def graph_allocated_vs_number_of_clients():
    data = pd.read_csv("csvs/allocations_test.csv")

    # Graph 1
    plt.figure(figsize=(12, 6))
    plt.subplot(1, 2, 1)
    plt.plot(data['NumberOfClients'], data['AllocatedClients'], marker='o', color='green')
    plt.title('Allocated Clients (Total Capacity = 20)')
    plt.xlabel('Number Of Clients')
    plt.ylabel('AllocatedClients')
    # plt.xticks(data['NumberOfClients'])
    plt.grid()

    # Graph 2
    plt.subplot(1, 2, 2)
    plt.plot(data['NumberOfClients'], data['NotAllocatedClients'], marker='o', color='red')
    plt.title('Not Allocated Clients (Total Capacity = 20)')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Not Allocated Clients')
    # plt.xticks(data['NumberOfClients'])
    plt.grid()

    plt.tight_layout()
    plt.savefig("images\\allocated_clients.png")
    plt.show()


# ---------NORMAL MATCHING---------------------------------------------------------------------

def graph_satisfaction_probability_random():
    data = pd.read_csv("csvs/random_test.csv")

    # Graph 1
    plt.figure(figsize=(12, 6))
    plt.subplot(1, 2, 1)
    plt.plot(data['NumberOfClients'], data['AverageSatisfactionProbability'], color='green')
    plt.title('Overall Probability of Meeting the Deadline for Clients - Random')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Overall Satisfaction')
    # plt.xticks(data['NumberOfClients'])
    plt.grid()

    # Graph 2
    plt.subplot(1, 2, 2)
    plt.plot(data['NumberOfClients'], data['AverageFailureProbability'], color='red')
    plt.title('Failure Probability - Random')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Failure Probability')
    # plt.xticks(data['NumberOfClients'])
    plt.grid()

    plt.tight_layout()
    plt.savefig("images\\normal_matching\\satisfaction_probability_random.png")
    plt.show()

def graph_satisfaction_probability_matching():
    data = pd.read_csv("csvs/matching_test.csv")

    # Graph 1
    plt.figure(figsize=(12, 6))
    plt.subplot(1, 2, 1)
    plt.plot(data['NumberOfClients'], data['AverageSatisfactionProbability'], color='green')
    plt.title('Overall Probability of Meeting the Deadline for Clients - Matching')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Overall Satisfaction')
    # plt.xticks(data['NumberOfClients'])
    plt.grid()

    # Graph 2
    plt.subplot(1, 2, 2)
    plt.plot(data['NumberOfClients'], data['AverageFailureProbability'], color='red')
    plt.title('Failure Probability - Matching')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Failure Probability')
    # plt.xticks(data['NumberOfClients'])
    plt.grid()

    plt.tight_layout()
    plt.savefig("images\\normal_matching\\satisfaction_probability_matching.png")
    plt.show()

def graph_satisfaction_random_vs_matching():
    data_random = pd.read_csv("csvs/random_test.csv")
    data_matching = pd.read_csv("csvs/matching_test.csv")

    plt.figure(figsize=(14, 6))

    plt.subplot(1, 2, 1)
    plt.plot(data_random['NumberOfClients'], data_random['AverageSatisfactionProbability'], color='red', label='Random')
    plt.plot(data_matching['NumberOfClients'], data_matching['AverageSatisfactionProbability'], color='blue', label='Matching')
    plt.title('Overall Probability of Meeting the Deadline for Clients (Random vs Matching)')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Overall Satisfaction')
    plt.legend()
    plt.grid()

    plt.subplot(1, 2, 2)
    plt.plot(data_random['NumberOfClients'], data_random['AverageFailureProbability'], color='red', label='Random')
    plt.plot(data_matching['NumberOfClients'], data_matching['AverageFailureProbability'], color='blue', label='Matching')
    plt.title('Failure Probability (Random vs Matching)')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Failure Probability')
    plt.legend()
    plt.grid()

    plt.tight_layout()
    plt.savefig("images\\normal_matching\\satisfaction_random_vs_matching.png")
    plt.show()

def graph_revenue_random_vs_matching():
    data_random = pd.read_csv("csvs/random_test.csv")
    data_matching = pd.read_csv("csvs/matching_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_random['NumberOfClients'], data_random['AverageRevenue'], color='red', label='Random')
    plt.plot(data_matching['NumberOfClients'], data_matching['AverageRevenue'], color='blue', label='Matching')
    plt.title('Cloud Providers Revenue (Random vs Matching)')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Total Revenue')
    plt.legend()
    plt.grid()

    plt.savefig("images\\normal_matching\\revenue_random_vs_matching.png")
    plt.show()

def graph_capacity_satisfaction_random_vs_matching():
    data_random = pd.read_csv("csvs/capacity_random_test.csv")
    data_matching = pd.read_csv("csvs/capacity_matching_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_random['TotalCapacity'], data_random['AverageSatisfactionProbability'], color='red', label='Random')
    plt.plot(data_matching['TotalCapacity'], data_matching['AverageSatisfactionProbability'], color='blue', label='Matching')
    plt.title('Impact of Varying the Number of Nodes on Satisfaction (Random vs Matching)')
    plt.xlabel('Total Capacity')
    plt.ylabel('Overall Satisfaction (60 Clients)')
    plt.legend()
    plt.grid()

    plt.savefig("images\\normal_matching\\capacity_satisfaction_random_vs_matching.png")
    plt.show()

def graph_capacity_revenue_random_vs_matching():
    data_random = pd.read_csv("csvs/capacity_random_test.csv")
    data_matching = pd.read_csv("csvs/capacity_matching_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_random['TotalCapacity'], data_random['AverageRevenue'], color='red', label='Random')
    plt.plot(data_matching['TotalCapacity'], data_matching['AverageRevenue'], color='blue', label='Matching')
    plt.title('Impact of Varying the Number of Nodes on Revenue (Random vs Matching)')
    plt.xlabel('Total Capacity')
    plt.ylabel('Total Revenue (60 Clients)')
    plt.legend()
    plt.grid()

    plt.savefig("images\\normal_matching\\capacity_revenue_random_vs_matching.png")
    plt.show()

def graph_satisfaction_match24_vs_match48():
    data_random = pd.read_csv("csvs/matching_cap48_test.csv")
    data_matching = pd.read_csv("csvs/matching_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_random['NumberOfClients'], data_random['AverageSatisfactionProbability'], color='red', label='Matching (Total capacity = 48)')
    plt.plot(data_matching['NumberOfClients'], data_matching['AverageSatisfactionProbability'], color='blue', label='Matching (Total capacity = 24)')
    plt.title('Comparison of Client Satisfaction for Total Capacities of 24 vs 48')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Overall Satisfaction')
    plt.legend()
    plt.grid()

    plt.savefig("images\\normal_matching\\satisfaction_match24_vs_match48.png")
    plt.show()

def graph_revenue_match24_vs_match48():
    data_random = pd.read_csv("csvs/matching_cap48_test.csv")
    data_matching = pd.read_csv("csvs/matching_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_random['NumberOfClients'], data_random['AverageRevenue'], color='red', label='Matching (Total capacity = 48)')
    plt.plot(data_matching['NumberOfClients'], data_matching['AverageRevenue'], color='blue', label='Matching (Total capacity = 24)')
    plt.title('Comparison of Cloud Provider Revenue for Total Capacities of 24 vs 48')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Total Revenue')
    plt.legend()
    plt.grid()

    plt.savefig("images\\normal_matching\\revenue_match24_vs_match48.png")
    plt.show()


# -------------EXTERNALITIES MATCHING-------------------------------------------------------

def graph_satisfaction_matching_whithout_vs_with_externalities_vs_random():
    data_externalities = pd.read_csv("csvs/matching_externalities_test.csv")
    data_without_externalities = pd.read_csv("csvs/matching_without_externalities_test.csv")
    data_random = pd.read_csv("csvs/random_externalities_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_externalities['NumberOfClients'], data_externalities['AverageSatisfactionProbability'], color='green', label='Matching with Externalities')
    plt.plot(data_without_externalities['NumberOfClients'], data_without_externalities['AverageSatisfactionProbability'], color='blue', label='Matching w/o Externalities')
    plt.plot(data_random['NumberOfClients'], data_random['AverageSatisfactionProbability'], color='red', label='Random')
    plt.title('Client Satisfaction Matching w/o Externalities vs Matching with Externalities')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Overall Satisfaction')
    plt.legend()
    plt.grid()

    plt.savefig("images\\externalities_matching\\satisfaction_matching_whithout_vs_with_externalities_vs_random.png")
    plt.show()

def graph_revenue_matching_without_vs_with_externalities_vs_random():
    data_externalities = pd.read_csv("csvs/matching_externalities_test.csv")
    data_without_externalities = pd.read_csv("csvs/matching_without_externalities_test.csv")
    data_random = pd.read_csv("csvs/random_externalities_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_externalities['NumberOfClients'], data_externalities['AverageRevenue'], color='green', label='Matching with Externalities')
    plt.plot(data_without_externalities['NumberOfClients'], data_without_externalities['AverageRevenue'], color='blue', label='Matching w/o Externalities')
    plt.plot(data_random['NumberOfClients'], data_random['AverageRevenue'], color='red', label='Random')
    plt.title('Revenue Matching w/o Externalities vs Matching with Externalities')
    plt.xlabel('Number Of Clients')
    plt.ylabel('Total Revenue')
    plt.legend()
    plt.grid()

    plt.savefig("images\\externalities_matching\\revenue_matching_without_vs_with_externalities_vs_random.png")
    plt.show()

def graph_capacity_revenue_matching_vs_externalities_vs_random():
    data_externalities = pd.read_csv("csvs/capacity_externalities_test.csv")
    data_random = pd.read_csv("csvs/capacity_random_externalities_test.csv")
    data_without_externalities = pd.read_csv("csvs/capacity_without_externalities_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_externalities['TotalCapacity'], data_externalities['AverageRevenue'], color='green', label='Matching with Externalities')
    plt.plot(data_random['TotalCapacity'], data_random['AverageRevenue'], color='red', label='Random')
    plt.plot(data_without_externalities['TotalCapacity'], data_without_externalities['AverageRevenue'], color='blue', label='Matching w/o Externalities')
    plt.title('Impact of Varying the Number of Nodes on Revenue (Random vs Matching)')
    plt.xlabel('Total Capacity')
    plt.ylabel('Total Revenue (60 Clients)')
    plt.legend()
    plt.grid()

    plt.savefig("images\\externalities_matching\\capacity_revenue_matching_vs_externalities.png")
    plt.show()

def graph_capacity_satisfaction_matching_vs_externalities_vs_random():
    data_externalities = pd.read_csv("csvs/capacity_externalities_test.csv")
    data_random = pd.read_csv("csvs/capacity_random_externalities_test.csv")
    data_without_externalities = pd.read_csv("csvs/capacity_without_externalities_test.csv")

    plt.figure(figsize=(8, 6))
    plt.plot(data_externalities['TotalCapacity'], data_externalities['AverageSatisfactionProbability'], color='green', label='Matching with Externalities')
    plt.plot(data_random['TotalCapacity'], data_random['AverageSatisfactionProbability'], color='red', label='Random')
    plt.plot(data_without_externalities['TotalCapacity'], data_without_externalities['AverageSatisfactionProbability'], color='blue', label='Matching w/o Externalities')
    plt.title('Impact of Varying the Number of Nodes on Satisfaction (Random vs Matching)')
    plt.xlabel('Total Capacity')
    plt.ylabel('Overall Satisfaction (60 Clients)')
    plt.legend()
    plt.grid()

    plt.savefig("images\\externalities_matching\\capacity_satisfaction_matching_vs_externalities.png")
    plt.show()

#graph_payment_distribution()
#graph_reachability_prob_vs_distance()
#graph_allocated_vs_number_of_clients()


#graph_satisfaction_probability_random()
#graph_satisfaction_probability_matching()
#graph_satisfaction_random_vs_matching()
#graph_revenue_random_vs_matching()
#graph_capacity_satisfaction_random_vs_matching()
#graph_capacity_revenue_random_vs_matching()
#graph_satisfaction_match24_vs_match48()
#graph_revenue_match24_vs_match48()


graph_satisfaction_matching_whithout_vs_with_externalities_vs_random()
graph_revenue_matching_without_vs_with_externalities_vs_random()
graph_capacity_revenue_matching_vs_externalities_vs_random()
graph_capacity_satisfaction_matching_vs_externalities_vs_random()